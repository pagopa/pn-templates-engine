package it.pagopa.pn.templatesengine.processor;

/**
 * Interfaccia per i processori di template.
 * Ogni implementazione opera su un oggetto di tipo {@code M} estratto dal model
 * e popola un oggetto di output tipizzato {@code O} (PROCESSED params).
 *
 * <p>Lo stesso processore può essere riutilizzato su più template,
 * associando mapper diversi per estrarre il campo target dal model.</p>
 *
 * @param <M> tipo dell'oggetto su cui il processore opera (es. CommunicationBody)
 * @param <O> tipo dell'oggetto output processato (es. InformalAnalogCommunicationProcessedParams.java)
 */
@FunctionalInterface
public interface TemplateModelProcessor<M, O> {
    /**
     * Elabora l'oggetto target e popola i parametri di output.
     *
     * @param model     l'oggetto estratto dal model tramite il mapper
     * @param outParams l'oggetto output tipizzato da popolare (accessibile nel template come PROCESSED)
     */
    void process(M model, O outParams);
}