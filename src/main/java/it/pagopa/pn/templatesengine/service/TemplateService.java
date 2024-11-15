package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
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
     * @param template il nome del template da eseguire.
     * @param language la lingua da utilizzare per il template.
     * @param objectModel il modello di dati da applicare al template.
     * @return un `Mono` contenente il risultato come stringa del template di testo, oppure un errore se il template
     *         non viene trovato.
     */
    public Mono<String> executeTextTemplate(TemplatesEnum template, LanguageEnum language, Object objectModel) {
        log.info("Execute TXT for template={},  language={} - START", template, language);
        return Mono.justOrEmpty(getFileName(template, language))
                .flatMap(templateFileName -> Mono.justOrEmpty(documentComposition.executeTextTemplate(templateFileName, objectModel)))
                .switchIfEmpty(Mono.error(new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, template.getTemplate())));
    }

    /**
     * Esegue un template in formato PDF utilizzando `DocumentComposition` e un modello di dati fornito, basandosi
     * sul nome del template e la lingua.
     *
     * @param template il nome del template da eseguire.
     * @param language la lingua da utilizzare per il template.
     * @param objectModel il modello di dati da applicare al template.
     * @return un `Mono` contenente il risultato come array di byte del PDF generato, oppure un errore se il template
     *         non viene trovato.
     */
    public Mono<byte[]> executePdfTemplate(TemplatesEnum template, LanguageEnum language, Object objectModel) {
        log.info("Execute Pdf for templateName={},  language={} - START", template, language);
        return Mono.justOrEmpty(getFileName(template, language))
                .flatMap(templateFileName -> Mono.justOrEmpty(documentComposition.executePdfTemplate(templateFileName, objectModel)))
                .switchIfEmpty(Mono.error(new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, template.getTemplate())));
    }

    /**
     * Recupera il nome del file del template corrispondente alla lingua specificata.
     *
     * @param template il nome del template di cui ottenere il file.
     * @param language la lingua per cui recuperare il file del template.
     * @return il nome del file del template per la lingua specificata.
     */
    private String getFileName(TemplatesEnum template, LanguageEnum language) {
        TemplateConfig.Template templates = templateConfig.getTemplates().get(template);
        return templates.getInput().get(language.getValue());
    }

    /**
     * Esegue un template definito come stringa, basandosi sul nome del template e la lingua,
     * senza applicare un modello di dati. Utilizzato per template memorizzati come testo semplice.
     *
     * @param template il nome del template di cui ottenere la stringa.
     * @param language la lingua da utilizzare per il template.
     * @return un `Mono` contenente il testo del template in formato stringa.
     */
    public Mono<String> executeTextTemplate(TemplatesEnum template, LanguageEnum language) {
        log.info("Execute templateAsString for templateName={},  language={} - START", template, language);
        TemplateConfig.Template templates = templateConfig.getTemplatesAsString().get(template);
        return Mono.just(templates.getInput().get(language.getValue()));
    }
}