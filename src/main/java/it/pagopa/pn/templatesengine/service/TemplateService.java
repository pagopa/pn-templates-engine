package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.TemplateNotFoundException;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

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
     * @param template    il nome del template da eseguire.
     * @param language    la lingua da utilizzare per il template.
     * @param objectModel il modello di dati da applicare al template.
     * @return un `Mono` contenente il risultato come stringa del template di testo, oppure un errore se il template
     * non viene trovato.
     */
    public <T> Mono<String> executeTextTemplate(TemplatesEnum template, LanguageEnum language, Mono<T> objectModel) {
        return objectModel.doOnNext(model -> log.info("Execute TXT for template={},  language={} - START", template, language))
                .flatMap(model -> {
                    String fileName = getFileName(template, language);
                    return Mono.fromCallable(() -> documentComposition.executeTextTemplate(fileName, model));
                })
                .doOnSuccess(result -> log.info("Execute TXT for templateName={}, language={} - COMPLETED", template, language))
                .doOnError(error -> log.error("Execute TXT for templateName={}, language={} - FAILED", template, language, error));
    }

    /**
     * Esegue un template in formato PDF utilizzando `DocumentComposition` e un modello di dati fornito, basandosi
     * sul nome del template e la lingua.
     *
     * @param template    il nome del template da eseguire.
     * @param language    la lingua da utilizzare per il template.
     * @param objectModel il modello di dati da applicare al template.
     * @return un `Mono` contenente il risultato come array di byte del PDF generato, oppure un errore se il template
     * non viene trovato.
     */
    public <T> Mono<byte[]> executePdfTemplate(TemplatesEnum template, LanguageEnum language, Mono<T> objectModel) {
        return objectModel.doOnNext(model -> log.info("Execute Pdf for templateName={},  language={} - START", template, language))
                .flatMap(model -> {
                    String fileName = getFileName(template, language);
                    return Mono.fromCallable(() -> documentComposition.executePdfTemplate(fileName, model));
                })
                .doOnSuccess(result -> log.info("Execute Pdf for templateName={}, language={} - COMPLETED", template, language))
                .doOnError(error -> log.error("Execute Pdf for templateName={}, language={} - FAILED", template, language, error));
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
        return Mono.defer(() -> {
            log.info("Execute templateAsString for templateName={}, language={} - START", template, language);
            TemplateConfig.Template templateAsString = templateConfig.getTemplatesAsString().get(template);
            if (templateAsString == null) {
                return Mono.error(templateNotFoundException(template));
            }
            String templateInput = templateAsString.getInput().get(language.getValue());
            if (templateInput == null) {
                String availableLanguages = availableLanguages(templateAsString.getInput());
                return Mono.error(templateNotFoundForLanguage(template, language, availableLanguages));
            }
            log.info("Execute templateAsString for templateName={}, language={} - COMPLETED", template, language);
            return Mono.just(templateInput);
        });
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
        if (templates == null) {
            templateNotFoundException(template);
        }
        String fileName = templates.getInput().get(language.getValue());
        if (fileName == null) {
            String availableLanguages = availableLanguages(templates.getInput());
            templateNotFoundForLanguage(template, language, availableLanguages);
        }
        return fileName;
    }

    private static Throwable templateNotFoundForLanguage(TemplatesEnum template, LanguageEnum language, String availableLanguages) {
        throw new TemplateNotFoundException(
                ExceptionTypeEnum.TEMPLATE_NOT_FOUND_FOR_LANGUAGE,
                template,
                String.format("Template not found for language '%s'. %s", language.getValue(), availableLanguages)
        );
    }

    private static Throwable templateNotFoundException(TemplatesEnum template) {
        throw new TemplateNotFoundException(
                ExceptionTypeEnum.TEMPLATE_NOT_FOUND_FOR_LANGUAGE,
                template,
                "Template not found: " + template.getTemplate());
    }

    private String availableLanguages(Map<String, String> input) {
        if (input == null || input.isEmpty()) {
            return "No languages available.";
        }
        return "Languages available for this template: " + String.join(", ", input.keySet());
    }
}