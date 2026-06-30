package it.pagopa.pn.templatesengine.model.params;

import lombok.Getter;
import lombok.Setter;

/**
 * Parametri processati in output per il template InformalAnalogCommunication.
 * Accessibili nel template FreeMarker come ${PROCESSED.primaryContentHtml}, ecc.
 */
@Getter
@Setter
public class InformalCommunicationGeneratedParams {
    private String primaryContentHtml;
    private String secondaryContentHtml;
}