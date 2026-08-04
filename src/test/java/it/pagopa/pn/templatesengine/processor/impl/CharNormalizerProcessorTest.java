package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationSender;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.SharedInformalCommunicationRecipient;
import it.pagopa.pn.templatesengine.model.InformalAnalogCommunicationGeneratedParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CharNormalizerProcessorTest {

    private CharNormalizerProcessor processor;
    private InformalAnalogCommunicationGeneratedParams outParams;

    @BeforeEach
    void setUp() {
        processor = new CharNormalizerProcessor();
        outParams = new InformalAnalogCommunicationGeneratedParams();
    }

    @Test
    void process_ShouldNormalizeAllConfiguredFields() {
        InformalCommunication model = new InformalCommunication();
        model.setSubject("Città d'Italia *");

        InformalCommunicationBody body = new InformalCommunicationBody();
        body.setPrimaryContent("Perché è già così: à è ç");
        body.setSecondaryContent("Š? no, ma š e ž");
        model.setBody(body);

        InformalCommunicationSender sender = new InformalCommunicationSender();
        sender.setDenomination("Comune di Česena");
        sender.setService("Servizio èlite");
        model.setSender(sender);

        SharedInformalCommunicationRecipient recipient = new SharedInformalCommunicationRecipient();
        recipient.setDenomination("Mario Dall'Ò");
        model.setRecipient(recipient);

        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();

        assertEquals(expectedByMap("Città d'Italia *"), outParams.getSubjectNormalized());
        assertEquals(expectedByMap("Perché è già così: à è ç"), outParams.getPrimaryContentNormalized());
        assertEquals(expectedByMap("Š? no, ma š e ž"), outParams.getSecondaryContentNormalized());
        assertEquals(expectedByMap("Comune di Česena"), outParams.getSenderDenominationNormalized());
        assertEquals(expectedByMap("Servizio èlite"), outParams.getSenderServiceNormalized());
        assertEquals(expectedByMap("Mario Dall'Ò"), outParams.getRecipientDenominationNormalized());
    }

    @Test
    void process_ShouldSetAllOutputFieldsToNull_WhenModelIsNull() {
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, null, outParams))
                .verifyComplete();
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, null, outParams))
                .verifyComplete();

        assertNull(outParams.getSubjectNormalized());
        assertNull(outParams.getPrimaryContentNormalized());
        assertNull(outParams.getSecondaryContentNormalized());
        assertNull(outParams.getSenderDenominationNormalized());
        assertNull(outParams.getSenderServiceNormalized());
        assertNull(outParams.getRecipientDenominationNormalized());
    }

    @Test
    void process_ShouldHandleNullNestedObjects() {
        InformalCommunication model = new InformalCommunication();
        model.setSubject("Solo subject");

        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();

        assertEquals("Solo subject", outParams.getSubjectNormalized());
        assertNull(outParams.getPrimaryContentNormalized());
        assertNull(outParams.getSecondaryContentNormalized());
        assertNull(outParams.getSenderDenominationNormalized());
        assertNull(outParams.getSenderServiceNormalized());
        assertNull(outParams.getRecipientDenominationNormalized());
    }

    private String expectedByMap(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String result = input;
        for (Map.Entry<String, String> e : CharNormalizerProcessor.getCharMap().entrySet()) {
            result = result.replace(e.getKey(), e.getValue());
        }
        return result;
    }
}