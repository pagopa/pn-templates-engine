package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Service per l'esecuzione e la gestione dei template, supportando sia i formati di testo che PDF.
 * Utilizza `DocumentComposition` per generare contenuti basati su template e `TemplateConfig` per configurare e
 * recuperare le informazioni sui template.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TemplateService {

    private final DocumentComposition documentComposition;
    private final TemplateConfig templateConfig;

    /**
     * Esegue un template di testo utilizzando `DocumentComposition` e un modello di dati fornito, basandosi sul
     * nome del template e la lingua.
     *
     * @param templateName il nome del template da eseguire.
     * @param language la lingua da utilizzare per il template.
     * @param objectModel il modello di dati da applicare al template.
     * @return un `Mono` contenente il risultato come stringa del template di testo, oppure un errore se il template
     *         non viene trovato.
     */
    public Mono<String> executeTextTemplate(String templateName, LanguageEnum language, Object objectModel) {
        log.info("Execute TXT for templateName={},  language={} - START", templateName, language);
        return Mono.justOrEmpty(getFileName(templateName, language))
                .flatMap(templateFileName -> Mono.justOrEmpty(documentComposition.executeTextTemplate(templateFileName, objectModel)))
                .switchIfEmpty(Mono.error(new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, templateName)));
    }

    /**
     * Esegue un template in formato PDF utilizzando `DocumentComposition` e un modello di dati fornito, basandosi
     * sul nome del template e la lingua.
     *
     * @param templateName il nome del template da eseguire.
     * @param language la lingua da utilizzare per il template.
     * @param objectModel il modello di dati da applicare al template.
     * @return un `Mono` contenente il risultato come array di byte del PDF generato, oppure un errore se il template
     *         non viene trovato.
     */
    public Mono<byte[]> executePdfTemplate(String templateName, LanguageEnum language, Object objectModel) {
        log.info("Execute Pdf for templateName={},  language={} - START", templateName, language);
        return Mono.justOrEmpty(getFileName(templateName, language))
                .flatMap(templateFileName -> Mono.justOrEmpty(documentComposition.executePdfTemplate(templateFileName, objectModel)))
                .switchIfEmpty(Mono.error(new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, templateName)));
    }

    /**
     * Recupera il nome del file del template corrispondente alla lingua specificata.
     *
     * @param templateName il nome del template di cui ottenere il file.
     * @param language la lingua per cui recuperare il file del template.
     * @return il nome del file del template per la lingua specificata.
     */
    private String getFileName(String templateName, LanguageEnum language) {
        TemplateConfig.Template template = templateConfig.getTemplates().get(templateName);
        return template.getInput().get(language.getValue());
    }

    /**
     * Esegue un template definito come stringa, basandosi sul nome del template e la lingua,
     * senza applicare un modello di dati. Utilizzato per template memorizzati come testo semplice.
     *
     * @param templateName il nome del template di cui ottenere la stringa.
     * @param language la lingua da utilizzare per il template.
     * @return un `Mono` contenente il testo del template in formato stringa.
     */
    public Mono<String> executeTextTemplate(String templateName, LanguageEnum language) {
        log.info("Execute templateAsString for templateName={},  language={} - START", templateName, language);
        TemplateConfig.Template template = templateConfig.getTemplatesAsString().get(templateName);
        return Mono.just(template.getInput().get(language.getValue()));
    }
}