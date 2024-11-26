package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Optional;

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
                    return Mono.fromCallable(() -> documentComposition.executeTextTemplate(fileName, model))
                            .subscribeOn(Schedulers.boundedElastic());
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
                    return Mono.fromCallable(() -> documentComposition.executePdfTemplate(fileName, model))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .doOnSuccess(result -> log.info("Execute Pdf for templateName={}, language={} - COMPLETED", template, language))
                .doOnError(error -> log.error("Execute Pdf for templateName={}, language={} - FAILED", template, language, error));
    }

    /**
     * Recupera il nome del file del template corrispondente alla lingua specificata.
     *
     * @param template il nome del template di cui ottenere il file.
     * @param language la lingua per cui recuperare il file del template.
     * @return il nome del file del template per la lingua specificata.
     */
    private String getFileName(TemplatesEnum template, LanguageEnum language) {
        return Optional.ofNullable(templateConfig.getTemplates().get(template))
                .map(TemplateConfig.Template::getInput)
                .map(inputMap -> inputMap.get(language.getValue()))
                .orElseThrow(() -> new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND,
                        ExceptionTypeEnum.TEMPLATE_NOT_FOUND.getMessage() + template.getTemplate() + ", lingua: "
                                + language.getValue(), HttpStatus.NOT_FOUND));
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
            log.info("Execute templateAsString for templateName={},  language={} - START", template, language);
            TemplateConfig.Template templates = templateConfig.getTemplatesAsString().get(template);
            String templateInput = Optional.ofNullable(templates)
                    .map(t -> t.getInput().get(language.getValue()))
                    .orElseThrow(() -> new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND,
                            ExceptionTypeEnum.TEMPLATE_NOT_FOUND.getMessage() + template.getTemplate() + ", lingua: "
                                    + language.getValue(), HttpStatus.NOT_FOUND));
            log.info("Execute templateAsString for templateName={},  language={} - COMPLETED", template, language);
            return Mono.just(templateInput);
        });
    }
}