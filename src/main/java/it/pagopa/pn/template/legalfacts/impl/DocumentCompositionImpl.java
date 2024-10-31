package it.pagopa.pn.template.legalfacts.impl;

import static it.pagopa.pn.template.exceptions.TemplateExceptionCodes.ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.stream.Stream;

import it.pagopa.pn.template.legalfacts.DocumentComposition;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@Slf4j
public class DocumentCompositionImpl implements DocumentComposition {

  private final Configuration freemarkerConfig;

  public DocumentCompositionImpl(Configuration freemarkerConfig) {
    this.freemarkerConfig = freemarkerConfig;
  }

  public Mono<String> executeTextTemplate(String content, Map<String, Object> mapTemplateModel) {
    log.info("Execute Text content={} START", content);
    try (StringWriter stringWriter = new StringWriter()) {
      var template = new Template(content, new StringReader(content), freemarkerConfig);
      template.process(mapTemplateModel, stringWriter);
      log.info("Execute Text content END");
      return Mono.just(stringWriter.getBuffer().toString());
    } catch (IOException | TemplateException exc) {
      throw new PnInternalException("Processing template",
              ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED, exc);
    }
  }

  public Flux<Byte> executePdfTemplate(String content, Map<String, Object> mapTemplateModel) {
    log.info("Pdf conversion start for templateName={} ", content);
    return executeTextTemplate(content, mapTemplateModel)
            .map(this::html2Pdf)
            .doOnNext((bytes) -> log.info("Pdf conversion done"))
            .flatMapMany(Flux::fromArray);
  }

  private Byte[] html2Pdf(String html) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document jsoupDoc = Jsoup.parse(html);
      W3CDom w3cDom = new W3CDom();
      org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(jsoupDoc);
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.usePdfUaAccessbility(true);
      builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_A);
      builder.withW3cDocument(w3cDoc, null);
      builder.toStream(baos);
      builder.run();
      return toByteArray(baos.toByteArray());
    } catch (IOException ex) {
      throw new PnInternalException("", "");
    }
  }

  private Byte[] toByteArray(byte[] array) {
    Byte[] bytes = new Byte[array.length];
    for(int i = 0; i < array.length; i++) {
      bytes[i] = array[i];
    }
    return bytes;
  }
}