package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Chain tipizzata di processori per un singolo template.
 * Incapsula il tipo del model in input ({@code MODEL}), il tipo dell'output ({@code O}),
 * la factory per creare l'output e la lista ordinata di step da eseguire.
 *
 * <p>Ogni step cattura in modo type-safe il processore e il mapper,
 * eliminando la necessità di cast o {@code @SuppressWarnings} nel codice chiamante.</p>
 *
 * @param <MODEL> tipo del model in input (es. InformalCommunication)
 * @param <O>     tipo dell'oggetto output generato (es. InformalCommunicationGeneratedParams)
 */
@Slf4j
public class TemplateProcessorChain<MODEL, O> {

    private final Class<MODEL> modelClass;
    private final Supplier<O> outputFactory;
    private final List<Step<MODEL, O>> steps = new ArrayList<>();

    public TemplateProcessorChain(Class<MODEL> modelClass, Supplier<O> outputFactory) {
        this.modelClass = modelClass;
        this.outputFactory = outputFactory;
    }

    /**
     * Aggiunge uno step alla chain.
     * Il mapper estrae dal model l'oggetto target, il processore lo elabora popolando l'output.
     *
     * @param processor processore da eseguire
     * @param mapper    funzione che estrae dal model l'oggetto su cui il processore opera
     * @param <M>       tipo dell'oggetto estratto dal mapper
     * @return questa chain, per registrazioni fluenti
     */
    public <M> TemplateProcessorChain<MODEL, O> add(TemplateModelProcessor<M, ? super O> processor,
                                                    Function<MODEL, M> mapper) {
        steps.add((template, model, output) -> {
            M target = mapper.apply(model);
            if (target != null) {
                return processor.process(template, target, output);
            }
            return Mono.empty();
        });
        log.info("Added processor {} to chain", processor.getClass().getSimpleName());
        return this;
    }

    /**
     * Esegue la chain: crea l'output tramite la factory, esegue tutti gli step in ordine,
     * e restituisce l'output popolato.
     *
     * @param template il template in fase di elaborazione
     * @param model    il model in input (viene castato in modo sicuro via {@code Class.cast})
     * @return un Mono con l'oggetto output popolato dai processori
     */
    Mono<Object> execute(TemplatesEnum template, Object model) {
        MODEL typedModel = modelClass.cast(model);
        O output = outputFactory.get();
        Mono<Void> chain = Mono.empty();
        for (Step<MODEL, O> step : steps) {
            chain = chain.then(step.execute(template, typedModel, output));
        }
        return chain.thenReturn(output);
    }

    @FunctionalInterface
    private interface Step<MODEL, O> {
        Mono<Void> execute(TemplatesEnum template, MODEL model, O output);
    }
}