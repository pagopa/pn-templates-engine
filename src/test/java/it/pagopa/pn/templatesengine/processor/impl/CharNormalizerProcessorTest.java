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

        assertEquals(expectedByMap("Città d'Italia *"), model.getSubject());
        assertEquals(expectedByMap("Perché è già così: à è ç"), model.getBody().getPrimaryContent());
        assertEquals(expectedByMap("Š? no, ma š e ž"), model.getBody().getSecondaryContent());
        assertEquals(expectedByMap("Comune di Česena"), model.getSender().getDenomination());
        assertEquals(expectedByMap("Servizio èlite"), model.getSender().getService());
        assertEquals(expectedByMap("Mario Dall'Ò"), model.getRecipient().getDenomination());
    }

    @Test
    void process_ShouldHandleNullModel() {
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, null, outParams))
                .verifyComplete();
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, null, outParams))
                .verifyComplete();
    }

    @Test
    void process_ShouldHandleNullNestedObjects() {
        InformalCommunication model = new InformalCommunication();
        model.setSubject("Solo subject");

        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();

        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();

        assertEquals("Solo subject", model.getSubject());
        assertNull(model.getBody());
        assertNull(model.getSender());
        assertNull(model.getRecipient());
    }

    @Test
    void process_ShouldNormalizeNullStringsAsNull() {
        InformalCommunication model = new InformalCommunication();
        model.setSubject(null);

        InformalCommunicationBody body = new InformalCommunicationBody();
        body.setPrimaryContent(null);
        body.setSecondaryContent(null);
        model.setBody(body);

        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();
        StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, model, outParams))
                .verifyComplete();

        assertNull(model.getSubject());
        assertNull(model.getBody().getPrimaryContent());
        assertNull(model.getBody().getSecondaryContent());
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