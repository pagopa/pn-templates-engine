package it.pagopa.pn.templatesengine.component.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.utils.TemplateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Si occupa della composizione di documenti a partire da template.
 * La classe utilizza FreeMarker per la generazione del testo e openhtmltopdf per la conversione da HTML a PDF.
 */
@Component
@Slf4j
@AllArgsConstructor
public class DocumentCompositionImpl implements DocumentComposition {

    private final Configuration freemarkerConfig;
    private final TemplateConfig templateConfig;

    /**
     * Genera un testo a partire da un template FreeMarker, identificato da templateName,
     * usando i dati contenuti in mapTemplateModel
     *
     * @param templateName     Nome del template da utilizzare.
     * @param mapTemplateModel Modello di dati per il rendering del template.
     * @return risultato del template processato
     */
    @Override
    public String executeTextTemplate(String templateName, Object mapTemplateModel) {
        return processTemplate(templateName, mapTemplateModel);
    }

    /**
     * Genera un PDF basato su un template FreeMarker, usando prima executeTextTemplate per ottenere il contenuto HTML,
     * che viene poi convertito in PDF tramite generatePdf.
     *
     * @param templateName     Nome del template da utilizzare.
     * @param mapTemplateModel Modello di dati per il rendering del template.
     * @return Un array di byte contenente il PDF generato.
     */
    @Override
    public byte[] executePdfTemplate(String templateName, Object mapTemplateModel) {
        String htmlMono = executeTextTemplate(templateName, mapTemplateModel);
        return generatePdf(htmlMono);
    }

    /**
     * Metodo ausiliario che elabora un template FreeMarker usando il modello
     * di dati fornito e restituisce il risultato come stringa di testo.
     *
     * @param templateName  Nome del template da caricare.
     * @param templateModel Dati per il rendering.
     * @return Una String contenente l'output del template processato.
     */
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

    /**
     * Converte una stringa HTML in un PDF, utilizzando openhtmltopdf e la
     * configurazione specificata per l’accessibilità e la conformità al formato PDF/A-3A.
     *
     * @param html Contenuto HTML da convertire in PDF.
     * @return Un array di byte rappresentante il PDF generato.
     */
    private byte[] generatePdf(String html) {
        log.info("Generating Pdf  - START");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String baseUri = TemplateUtils.getFormattedPath(templateConfig.getTemplatesPath());
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