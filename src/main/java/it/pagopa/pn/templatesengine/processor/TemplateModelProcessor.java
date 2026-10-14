package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import reactor.core.publisher.Mono;

/**
 * Interfaccia funzionale per i processori di template.
 *
 * <p>Ogni implementazione opera su un oggetto di tipo {@code M} estratto dal model
 * e popola un oggetto di output tipizzato {@code O}, accessibile nel template FreeMarker
 * come variabile {@code GENERATED}.</p>
 *
 * <p>Il tipo {@code O} è definito come inner interface ({@code OutputModel}) all'interno
 * di ogni processore concreto, in modo che il contratto di output sia co-locato
 * con il processore che lo utilizza. I model di output implementano le interfacce
 * dei processori che li popolano, consentendo composizione via multi-implementazione.</p>
 *
 * <p>Lo stesso processore può essere riutilizzato su più template,
 * associando mapper diversi per estrarre il campo target dal model.</p>
 *
 * @param <M> tipo dell'oggetto su cui il processore opera (es. {@code InformalCommunicationBody})
 * @param <O> contratto di output del processore (es. {@code MarkdownToHtmlProcessor.OutputModel})
 */
@FunctionalInterface
public interface TemplateModelProcessor<M, O> {
    /**
     * Elabora l'oggetto target e popola i parametri di output.
     *
     * @param template  il template in fase di elaborazione
     * @param model     l'oggetto estratto dal model tramite il mapper
     * @param outParams l'oggetto output tipizzato da popolare (accessibile nel template come {@code GENERATED})
     * @return un Mono che completa quando l'elaborazione è terminata
     */
    Mono<Void> process(TemplatesEnum template, M model, O outParams);
}