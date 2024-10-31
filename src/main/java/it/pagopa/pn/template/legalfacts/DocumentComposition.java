package it.pagopa.pn.template.legalfacts;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class DocumentComposition {

  private final Configuration freemarkerConfig;

  public String executeTextTemplate(String content, Map<String, Object> mapTemplateModel) {
    log.info("Execute Text content={} START", content);
    try (StringWriter stringWriter = new StringWriter()) {
      var template = new Template(content, new StringReader(content), freemarkerConfig);
      template.process(mapTemplateModel, stringWriter);
      log.info("Execute Text content END");
      return stringWriter.getBuffer().toString();
    } catch (IOException | TemplateException exc) {
      throw new PnInternalException("Processing template",
          ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED, exc);
    }
  }

  public byte[] executePdfTemplate(String content, Map<String, Object> mapTemplateModel) {
    String html = executeTextTemplate(content, mapTemplateModel);
    // TODO baseUris
    String baseUri = "";
    log.info("Pdf conversion start for templateName={} with baseUri={}", content, baseUri);
    byte[] pdf = html2Pdf(baseUri, html);
    log.info("Pdf conversion done");
    return pdf;
  }

  private byte[] html2Pdf(String baseUri, String html) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
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
    } catch (IOException ex) {
      throw new PnInternalException("", "");
    }
  }

}
