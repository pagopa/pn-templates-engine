package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry che associa ogni template (TemplatesEnum) a una {@link TemplateProcessorChain} tipizzata.
 *
 * <p>Ogni chain contiene la factory per creare l'oggetto output e la lista ordinata di processori.
 * L'esecuzione è completamente type-safe: nessun cast esplicito né {@code @SuppressWarnings}.</p>
 */
@Slf4j
@Component
public class TemplateProcessorRegistry {

    private final Map<TemplatesEnum, TemplateProcessorChain<?, ?>> chains = new HashMap<>();

    /**
     * Registra una nuova chain per il template specificato.
     *
     * @param template      il template a cui associare la chain
     * @param modelClass    la classe del model in input
     * @param outputFactory supplier che crea una nuova istanza dell'oggetto output
     * @param <MODEL>       tipo del model in input
     * @param <O>           tipo dell'oggetto output generato
     * @return la chain appena creata, per registrazioni fluenti con {@link TemplateProcessorChain#add}
     */
    public <MODEL, O> TemplateProcessorChain<MODEL, O> registerChain(TemplatesEnum template,
                                                                     Class<MODEL> modelClass,
                                                                     Supplier<O> outputFactory) {
        var chain = new TemplateProcessorChain<>(modelClass, outputFactory);
        chains.put(template, chain);
        log.info("Registered processor chain for template {}", template);
        return chain;
    }

    /**
     * Esegue la chain registrata per il template: crea l'output, esegue i processori
     * e restituisce l'oggetto output popolato.
     *
     * @param template il template in fase di elaborazione
     * @param model    il model in input
     * @return l'oggetto output popolato, oppure {@code null} se nessuna chain è registrata
     */
    public Object executeProcessors(TemplatesEnum template, Object model) {
        TemplateProcessorChain<?, ?> chain = chains.get(template);
        if (chain == null) {
            return null;
        }
        return chain.execute(template, model);
    }
}