package it.pagopa.pn.templates.engine.component.impl;

import static it.pagopa.pn.templates.engine.exceptions.TemplateExceptionCodes.ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import it.pagopa.pn.templates.engine.dao.FileDao;
import it.pagopa.pn.templates.engine.component.DocumentComposition;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class DocumentCompositionImpl implements DocumentComposition {

  private final Configuration freemarkerConfig;
  private final FileDao fileDao;
  private final TemplateConfig templateConfig;

  public DocumentCompositionImpl(Configuration freemarkerConfig, FileDao fileDao,
      TemplateConfig templateConfig) {
    this.freemarkerConfig = freemarkerConfig;
    this.fileDao = fileDao;
    this.templateConfig = templateConfig;
  }

  public String executeTextTemplate(String templateName, Map<String, Object> mapTemplateModel) {
    var content = this.fileDao.getFile(templateConfig.getPath() + templateName);
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

  public byte[] executePdfTemplate(String templateName, Map<String, Object> mapTemplateModel) {
    String html = executeTextTemplate(templateName, mapTemplateModel);
    log.info("Pdf conversion start for templateName={} ", html);
    byte[] pdf = html2Pdf(html);
    log.info("Pdf conversion done");
    return pdf;
  }

  private byte[] html2Pdf(String html) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      Document jsoupDoc = Jsoup.parse(html);
      W3CDom w3cDom = new W3CDom();
      org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(jsoupDoc);
      PdfRendererBuilder builder = new PdfRendererBuilder();
      builder.usePdfUaAccessbility(true);
      builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_A);
      Path basePath = Paths.get(templateConfig.getFontPath());
      URI baseUri = basePath.toUri();
      builder.withW3cDocument(w3cDoc, baseUri.toString());
      builder.toStream(baos);
      builder.run();
      return baos.toByteArray();
    } catch (IOException ex) {
      throw new PnInternalException("", "");
    }
  }

  private Byte[] toByteArray(byte[] array) {
    Byte[] bytes = new Byte[array.length];
    for (int i = 0; i < array.length; i++) {
      bytes[i] = array[i];
    }
    return bytes;
  }
}