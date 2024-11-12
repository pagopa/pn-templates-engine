package it.pagopa.pn.templates.engine.component.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import static it.pagopa.pn.templates.engine.utils.TemplateUtils.getFormattedPath;

@Component
@Slf4j
@AllArgsConstructor
public class DocumentCompositionImpl implements DocumentComposition {

    private final Configuration freemarkerConfig;
    private final TemplateConfig templateConfig;

    @Override
    public String executeTextTemplate(String templateName, Map<String, Object> mapTemplateModel) {
        return processTemplate(templateName, mapTemplateModel);
    }

    @Override
    public byte[] executePdfTemplate(String templateName, Map<String, Object> mapTemplateModel) {
        String htmlMono = executeTextTemplate(templateName, mapTemplateModel);
        return generatePdf(htmlMono);
    }

    private String processTemplate(String templateName, Object templateModel) {
        try {
            log.info("Conversion on Text - START");
            StringWriter stringWriter = new StringWriter();
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(templateModel, stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (TemplateException | IOException ex) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION, ex.getMessage());
        }
    }

    private byte[] generatePdf(String html) {
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
        } catch (IOException ex) {
            throw new PnInternalException(ex.getMessage(), ExceptionTypeEnum.ERROR_PDF_DOCUMENT_GENERATION.getMessage());
        }
    }
}