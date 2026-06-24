package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

/**
 * Registry che associa ogni template (TemplatesEnum) a una lista ordinata di processori.
 * Ogni entry contiene un processore e un mapper per estrarre dal model l'oggetto target.
 *
 * <p>I processori vengono eseguiti in sequenza nell'ordine di registrazione,
 * operando sull'oggetto estratto dal mapper e popolando l'output tipizzato (PROCESSED).</p>
 */
@Slf4j
@Component
public class TemplateProcessorRegistry {
    private final Map<TemplatesEnum, List<ProcessorEntry<?, ?, ?>>> registry = new HashMap<>();
    /**
     * Registra un processore per un template specifico, con un mapper per estrarre
     * il campo target dal model.
     *
     * @param template  il template a cui associare il processore
     * @param processor il processore da eseguire
     * @param mapper    funzione che estrae l'oggetto target dal model
     * @param <MODEL>   tipo del model completo
     * @param <M>       tipo dell'oggetto target su cui opera il processore
     * @param <O>       tipo dell'oggetto output processato
     */
    public <MODEL, M, O> void register(TemplatesEnum template,
                                       TemplateModelProcessor<M, O> processor,
                                       Function<MODEL, M> mapper) {
        registry.computeIfAbsent(template, k -> new ArrayList<>())
                .add(new ProcessorEntry<>(processor, mapper));
        log.info("Registered processor {} for template {}", processor.getClass().getSimpleName(), template);
    }
    /**
     * Esegue tutti i processori registrati per il template.
     * Ogni processore riceve l'oggetto estratto dal model e l'oggetto output da popolare.
     *
     * @param template       il template in fase di elaborazione
     * @param model          il model in input
     * @param processedModel l'oggetto output tipizzato da popolare (PROCESSED)
     * @param <MODEL>        tipo del model completo
     * @param <O>            tipo dell'oggetto output processato
     */
    @SuppressWarnings("unchecked")
    public <MODEL, O> void executeProcessors(TemplatesEnum template, MODEL model, O processedModel) {
        List<ProcessorEntry<?, ?, ?>> entries = registry.getOrDefault(template, Collections.emptyList());
        for (ProcessorEntry<?, ?, ?> entry : entries) {
            ((ProcessorEntry<MODEL, ?, O>) entry).execute(model, processedModel);
        }
    }
    /**
     * Entry interna che associa un processore al suo mapper.
     *
     * @param <MODEL> tipo del model completo
     * @param <M>     tipo dell'oggetto target estratto dal mapper
     * @param <O>     tipo dell'oggetto output processato
     */
    private record ProcessorEntry<MODEL, M, O>(TemplateModelProcessor<M, O> processor, Function<MODEL, M> mapper) {
        void execute(MODEL model, O outParams) {
            M target = mapper.apply(model);
            if (target != null) {
                processor.process(target, outParams);
            }
        }
    }
}