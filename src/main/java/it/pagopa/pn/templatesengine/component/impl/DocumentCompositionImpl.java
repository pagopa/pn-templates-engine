package it.pagopa.pn.templatesengine.component.impl;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

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
     * Genera un testo a partire da un template FreeMarker, identificato da templateFile,
     * applicando i dati forniti tramite templateModel
     *
     * @param templateFile  Nome del template da utilizzare.
     * @param templateModel Modello di dati per il rendering del template.
     * @return risultato del template processato
     */
    @Override
    public String executeTextTemplate(String templateFile, Object templateModel) {
        String htmlContent = processTemplate(templateFile, templateModel);
        log.info("Conversion on Text, templateFile={} - COMPLETED", templateFile);
        return htmlContent;
    }

    /**
     * Genera un documento PDF a partire da un template FreeMarker.
     * Prima esegue la generazione del contenuto HTML tramite il metodo `executeTextTemplate`,
     * e successivamente lo converte in PDF.
     *
     * @param templateFile  Nome del template da utilizzare.
     * @param templateModel Modello di dati per il rendering del template.
     * @return Un array di byte contenente il PDF generato.
     */
    @Override
    public byte[] executePdfTemplate(String templateFile, Object templateModel) {
        String htmlContent = executeTextTemplate(templateFile, templateModel);
        return generatePdf(htmlContent, templateFile);
    }

    /**
     * Elabora un template FreeMarker utilizzando il modello di dati fornito,
     * restituendo il risultato come stringa.
     *
     * @param templateFile  Nome del template da caricare.
     * @param templateModel Dati per il rendering.
     * @return Una String contenente l'output del template processato.
     * @throws PnGenericException In caso di errori durante il rendering del template.
     */
    private String processTemplate(String templateFile, Object templateModel) {
        log.info("Conversion on Text, templateFile={} - START", templateFile);
        try (StringWriter stringWriter = new StringWriter()) {
            Template template = freemarkerConfig.getTemplate(templateFile);
            template.process(templateModel, stringWriter);
            return stringWriter.toString();
        } catch (TemplateException | IOException ex) {
            throw new PnGenericException(
                    ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION,
                    ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Converte una stringa HTML in un PDF, utilizzando openhtmltopdf e la
     * configurazione specificata per l’accessibilità e la conformità al formato PDF/A-3A.
     *
     * @param html Contenuto HTML da convertire in PDF.
     * @return Un array di byte rappresentante il PDF generato.
     * @throws PnGenericException In caso di errori durante la generazione del PDF.
     */
    private byte[] generatePdf(String html, String templateFile) {
        log.info("Generating Pdf  - START");
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            String baseUri = TemplateUtils.getBaseURI(templateConfig.getTemplatesPath(), templateFile);
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
            throw new PnGenericException(
                    ExceptionTypeEnum.ERROR_PDF_DOCUMENT_GENERATION, ex.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}