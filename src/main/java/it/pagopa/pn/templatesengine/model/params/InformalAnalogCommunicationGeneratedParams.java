package it.pagopa.pn.templatesengine.model.params;

import it.pagopa.pn.templatesengine.processor.impl.InformalSenderLogoProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * Parametri processati in output per i template con contenuto HTML + logo del mittente.
 * Accessibili nel template FreeMarker come ${GENERATED.primaryContentHtml}, ${GENERATED.logoBase64}, ecc.
 */
@Getter
@Setter
public class InformalAnalogCommunicationGeneratedParams
        extends InformalCommunicationGeneratedParams
        implements InformalSenderLogoProcessor.OutputModel {
    private String senderLogoBase64;
}
