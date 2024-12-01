package it.pagopa.pn.templatesengine.rest;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Stream;

@SpringBootTest
@AutoConfigureMockMvc
public class TemplateApiControllerTestIT {

    public static final String ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT = "/templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact";
    public static final String SMS_VERIFICATION_CODE_BODY = "/templates-engine-private/v1/templates/sms-verification-code-body";

    @Autowired
    WebTestClient webTestClient;

    @ParameterizedTest
    @MethodSource("executePdfTemplateTest")
    void testPdfTemplateWebClient(
            String testName,
            Object request,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus
    ) {
        Flux.range(1, 10) // Genera 10 iterazioni
                .flatMap(i -> Mono.defer(() -> Mono.just(webTestClient.put()
                        .uri(testName)
                        .accept(mediaType)
                        .header(HttpHeaders.ACCEPT, mediaType.toString())
                        .header("x-language", language.getValue())
                        .body(Mono.just(request), request.getClass()) // Specifica esplicitamente il tipo di body
                        .exchange()
                        .expectStatus()
                        .isEqualTo(expectedStatus)
                        .expectBody(ByteArrayResource.class) // Specifica il tipo atteso nella risposta
                        .value(resource -> {
                            byte[] responseData = resource.getByteArray();
                            Assertions.assertNotNull(responseData, "Response data should not be null");
                        }))))
                .parallel() // Abilita il parallelismo
                .runOn(Schedulers.parallel()) // Usa un thread scheduler per eseguire le chiamate in parallelo
                .sequential() // Ritorna a un flusso sequenziale dopo l'elaborazione parallela
                .blockLast();// Aspetta che tutte le chiamate siano completate
    }

    @ParameterizedTest
    @MethodSource("executeTxtTemplateTestNoBody")
    void testTxtTemplateWebClientNoBody(
            String testName,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus
    ) {
        // Act & Assert
        Flux.range(1, 10) // Genera 10 iterazioni
                .flatMap(i -> Mono.defer(() -> Mono.just(webTestClient.put()
                        .uri(testName)
                        .accept(mediaType)
                        .header(HttpHeaders.ACCEPT, mediaType.toString())
                        .header("x-language", language.getValue())
                        .exchange()
                        .expectStatus()
                        .isEqualTo(expectedStatus)
                        .expectBody(String.class) // Specifica il tipo atteso nella risposta
                        .value(resource ->
                            Assertions.assertNotNull(resource, "Response data should not be null")
                        ))))
                .parallel() // Abilita il parallelismo
                .runOn(Schedulers.parallel()) // Usa un thread scheduler per eseguire le chiamate in parallelo
                .sequential() // Ritorna a un flusso sequenziale dopo l'elaborazione parallela
                .blockLast();// Aspetta che tutte le chiamate siano completate
    }

    private static Stream<Arguments> executeTxtTemplateTestNoBody() {
        return Stream.of(
                Arguments.of(
                        SMS_VERIFICATION_CODE_BODY,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED
                )
        );
    }

    private static Stream<Arguments> executePdfTemplateTest() {
        var recipient = new AnalogDeliveryWorkflowFailureRecipient();
        recipient.setDenomination("Denomination_test");
        recipient.setTaxId("TAX_ID_TEST");
        var analogDeliveryWorkflowFailureLegalFact = new AnalogDeliveryWorkflowFailureLegalFact()
                .endWorkflowDate("endWorkflowDate_TEST").iun("TEST").endWorkflowTime("endWorkflowTime_TEST");
        analogDeliveryWorkflowFailureLegalFact.recipient(recipient);

        return Stream.of(
                Arguments.of(
                        ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT,
                        analogDeliveryWorkflowFailureLegalFact,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED
                )
        );
    }

}
