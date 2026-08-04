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

    private String subjectNormalized;
    private String primaryContentNormalized;
    private String secondaryContentNormalized;
    private String senderDenominationNormalized;
    private String senderServiceNormalized;
    private String recipientDenominationNormalized;

    @Override
    public void setSubjectNormalized(String value) {
        this.subjectNormalized = value;
    }

    @Override
    public void setPrimaryContentNormalized(String value) {
        this.primaryContentNormalized = value;
    }

    @Override
    public void setSecondaryContentNormalized(String value) {
        this.secondaryContentNormalized = value;
    }

    @Override
    public void setSenderDenominationNormalized(String value) {
        this.senderDenominationNormalized = value;
    }

    @Override
    public void setSenderServiceNormalized(String value) {
        this.senderServiceNormalized = value;
    }

    @Override
    public void setRecipientDenominationNormalized(String value) {
        this.recipientDenominationNormalized = value;
    }
}
