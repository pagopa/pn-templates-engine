package it.pagopa.pn.templatesengine.model;

import it.pagopa.pn.templatesengine.processor.impl.CharNormalizerProcessor;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * Parametri processati in output per i template con contenuto HTML + logo del mittente.
 * Accessibili nel template FreeMarker come ${GENERATED.primaryContentHtml}, ${GENERATED.senderLogoBase64}, ecc.
 */
@Getter
@Setter
public class InformalAnalogCommunicationGeneratedParams
        extends InformalCommunicationGeneratedParams
        implements SenderLogoProcessor.OutputModel,
        CharNormalizerProcessor.OutputModel {

    private String senderLogoBase64;
}
