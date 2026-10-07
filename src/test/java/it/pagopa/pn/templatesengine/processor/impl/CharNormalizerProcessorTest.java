package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationSender;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.SharedInformalCommunicationRecipient;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CharNormalizerProcessorTest {

    private CharNormalizerProcessor processor;
    private InformalCommunicationGeneratedParams outParams;

    @BeforeEach
    void setUp() {
        processor = new CharNormalizerProcessor();
        outParams = new InformalCommunicationGeneratedParams();
    }

    @Test
    void process_ShouldNormalizeAllConfiguredFields() {
        InformalCommunication model = new InformalCommunication();
        model.setSubject("Città d'Italia *");

        InformalCommunicationBody body = new InformalCommunicationBody();
        body.setPrimaryContent("Perché è già così: à è ç");
        body.setSecondaryContent("Š? no, ma š e ž");
        outParams.setPrimaryContentHtml(body.getPrimaryContent());
        outParams.setSecondaryContentHtml(body.getSecondaryContent());
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
        assertEquals(expectedByMap("Perché è già così: à è ç"), outParams.getPrimaryContentHtml());
        assertEquals(expectedByMap("Š? no, ma š e ž"), outParams.getSecondaryContentHtml());
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

    /**
     * Mappa di riferimento usata dal test per calcolare il valore atteso della normalizzazione.
     * Contiene le entity attese per i caratteri speciali/diacritici gestiti esplicitamente;
     * gli altri caratteri non-ASCII vengono convertiti in entity esadecimali da {@link #expectedByMap(String)},
     * replicando il comportamento di {@link CharNormalizerProcessor#normalize(String)}.
     */
    private static final Map<String, String> CHAR_MAP = Map.ofEntries(
            Map.entry("č", "&#x10D;"),
            Map.entry("Č", "&#x10C;"),
            Map.entry("š", "&#x161;"),
            Map.entry("ž", "&#x17E;"),
            Map.entry("Ž", "&#x17D;"),
            Map.entry("'", "&#39;"),
            Map.entry("é", "&#xE9;"),
            Map.entry("è", "&#xE8;"),
            Map.entry("ê", "&#xEA;"),
            Map.entry("È", "&#xC8;"),
            Map.entry("ç", "&#xE7;"),
            Map.entry("à", "&#xE0;"),
            Map.entry("À", "&#xC0;"),
            Map.entry("ù", "&#xF9;"),
            Map.entry("û", "&#xFB;"),
            Map.entry("ô", "&#xF4;"),
            Map.entry("î", "&#xEE;"),
            Map.entry("ü", "&#xFC;"),
            Map.entry("ö", "&#xF6;"),
            Map.entry("Ö", "&#xD6;"),
            Map.entry("ä", "&#xE4;"),
            Map.entry("Ä", "&#xC4;"),
            Map.entry("*", "&#42;")
    );

    private String expectedByMap(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder(input.length());
        input.codePoints().forEach(codePoint -> {
            String character = new String(Character.toChars(codePoint));
            String mapped = CHAR_MAP.get(character);
            if (mapped != null) {
                result.append(mapped);
            } else if (codePoint > 0x7F) {
                result.append("&#x").append(Integer.toHexString(codePoint).toUpperCase()).append(";");
            } else {
                result.appendCodePoint(codePoint);
            }
        });
        return result.toString();
    }
}