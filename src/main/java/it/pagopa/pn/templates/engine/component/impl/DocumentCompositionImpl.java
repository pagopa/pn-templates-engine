package it.pagopa.pn.templates.engine.component.impl;

import static it.pagopa.pn.templates.engine.utils.TemplateUtils.getFormattedPath;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import it.pagopa.pn.templates.engine.component.DocumentComposition;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import it.pagopa.pn.templates.engine.exceptions.*;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


@Component
@Slf4j
public class DocumentCompositionImpl implements DocumentComposition {
  private final Configuration freemarkerConfig;
  private final TemplateConfig templateConfig;

  public DocumentCompositionImpl(Configuration freemarkerConfig, TemplateConfig templateConfig) {
    this.freemarkerConfig = freemarkerConfig;
    this.templateConfig = templateConfig;
  }


  @Override
  public Mono<String> executeTextTemplate(String templateName, Mono<Map<String, Object>> mapTemplateModel) {
    return mapTemplateModel
            .doOnNext(this::printMappedTemplate)
            .flatMap(templateModel -> Mono.fromCallable(() -> processTemplate(templateName, templateModel))
                    .subscribeOn(Schedulers.boundedElastic()))
            .doOnNext(value -> log.info("Conversion on Text success - END"))
            .onErrorResume(exception -> Mono.error(new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION, exception.getMessage())));
  }

  @Override
  public Mono<byte[]> executePdfTemplate(String templateName, Mono<Map<String, Object>> mapTemplateModel) {
    Mono<String> htmlMono = executeTextTemplate(templateName, mapTemplateModel);
    return html2Pdf(htmlMono)
            .doOnNext(value -> log.info("Pdf conversion success - END"));
  }

  private String processTemplate(String templateName, Object templateModel) throws TemplateException, IOException {
    log.info("Conversion on Text - START");
    StringWriter stringWriter = new StringWriter();
    Template template = freemarkerConfig.getTemplate(templateName);
    template.process(templateModel, stringWriter);
    return stringWriter.getBuffer().toString();
  }

  private Mono<byte[]> html2Pdf(Mono<String> htmlMono) {
    return htmlMono
            .doOnNext(value -> log.info("Pdf conversion - START"))
            .flatMap(html -> Mono.fromCallable(() -> generatePdf(html)))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnNext(value -> log.info("Generating Pdf - END"))
            .onErrorResume(exception -> Mono.error(new PnGenericException(ExceptionTypeEnum.ERROR_PDF_DOCUMENT_GENERATION, exception.getMessage())));
  }

  private byte[] generatePdf(String html) throws IOException {
    log.info("Generating Pdf  - START");
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      String baseUri = getFormattedPath(templateConfig.getTemplatesPath());
      Document jsoupDoc = Jsoup.parse(html);
      W3CDom w3cDom = new W3CDom();
      org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(jsoupDoc);
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.usePdfUaAccessbility(true);
      builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_A);
      builder.withW3cDocument(w3cDoc, baseUri);
      builder.toStream(baos);
      builder.run();
      return baos.toByteArray();
    }
  }

  private void printMappedTemplate(Map<String, Object> mappedTemplate) {
    log.info("Template mapped={}", mappedTemplate);
  }
}