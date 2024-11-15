package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.config.TemplatesEnum;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templates.engine.service.TemplateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@WebFluxTest(TemplateApiControllerTest.class)
public class TemplateApiControllerTest {

    public static final String EXPECTED_RESULT = "executeTxtTemplateTest";

    public static final String NOTIFICATION_RECEIVED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-received-legal-fact";
    public static final String PEC_DELIVERY_WORKFLOW_LEGAL_FACT = "/templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact";
    public static final String NOTIFICATION_CANCELLED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-cancelled-legal-fact";
    public static final String NOTIFICATION_AAR = "/templates-engine-private/v1/templates/notification-aar";
    public static final String NOTIFICATION_AAR_RADDALT = "/templates-engine-private/v1/templates/notification-aar-radd-alt";
    public static final String ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT = "/templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact";
    public static final String NOTIFICATION_AARFOR_EMAIL = "/templates-engine-private/v1/templates/notification-aar-for-email";
    public static final String NOTIFICATION_AARFOR_PEC = "/templates-engine-private/v1/templates/notification-aar-for-pec";
    public static final String EMAILBODY = "/templates-engine-private/v1/templates/emailbody";
    public static final String PECBODY = "/templates-engine-private/v1/templates/pecbody";
    public static final String PEC_BODY_CONFIRM = "/templates-engine-private/v1/templates/pecbodyconfirm";
    public static final String PECBODYREJECT = "/templates-engine-private/v1/templates/pecbodyreject";
    public static final String NOTIFICATION_VIEWED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-viewed-legal-fact";
    public static final String NOTIFICATION_AARSUBJECT = "/templates-engine-private/v1/templates/notification-aar-subject";
    public static final String EMAILSUBJECT = "/templates-engine-private/v1/templates/emailsubject";
    public static final String PECSUBJECT = "/templates-engine-private/v1/templates/pecsubject";
    public static final String PECSUBJECTCONFIRM = "/templates-engine-private/v1/templates/pecsubjectconfirm";
    public static final String PECSUBJECTREJECT = "/templates-engine-private/v1/templates/pecsubjectreject";
    public static final String LEGAL_FACT_MALFUNCTION = "/templates-engine-private/v1/templates/legal-fact-malfunction";
    public static final String NOTIFICATION_AAR_FOR_SMS = "/templates-engine-private/v1/templates/notification-aar-for-sms";
    public static final String SMSBODY = "/templates-engine-private/v1/templates/smsbody";

    @Autowired
    WebTestClient webTestClient;
    @MockBean
    TemplateService templateService;

    @ParameterizedTest
    @MethodSource("executePdfTemplateTest")
    void testPdfTemplateWebClient(
            String testName,
            String templateName,
            Object request,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus,
            byte[] expectedData
    ) {
        // Arrange
        Mockito.when(templateService.executePdfTemplate(templateName, language, request))
                .thenReturn(Mono.just(expectedData));
        // Act & Assert
        webTestClient.put()
                .uri(testName.toLowerCase())
                .accept(mediaType)
                .header(HttpHeaders.ACCEPT, mediaType.toString())
                .header("x-language", language.getValue())
                .body(Mono.just(request), request.getClass())
                .exchange()
                .expectStatus()
                .isEqualTo(expectedStatus)
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assertArrayEquals(expectedData, responseData);
                });
    }

    @ParameterizedTest
    @MethodSource("executeTxtTemplateTest")
    void testTxtTemplateWebClient(
            String testName,
            String templateName,
            Object request,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus,
            String expectedData
    ) {
        // Arrange
        Mockito.when(templateService.executeTextTemplate(templateName, language, request))
                .thenReturn(Mono.just(expectedData));
        // Act & Assert
        webTestClient.put()
                .uri(testName.toLowerCase())
                .accept(mediaType)
                .header(HttpHeaders.ACCEPT, mediaType.toString())
                .header("x-language", language.getValue())
                .body(Mono.just(request), request.getClass())
                .exchange()
                .expectStatus()
                .isEqualTo(expectedStatus)
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(EXPECTED_RESULT, resource));
    }

    @ParameterizedTest
    @MethodSource("executeTxtTemplateTestNoBody")
    void testTxtTemplateWebClientNoBody(
            String testName,
            String templateName,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus,
            String expectedData
    ) {
        // Arrange
        Mockito.when(templateService.executeTextTemplate(templateName, language))
                .thenReturn(Mono.just(expectedData));
        // Act & Assert
        webTestClient.put()
                .uri(testName.toLowerCase())
                .accept(mediaType)
                .header(HttpHeaders.ACCEPT, mediaType.toString())
                .header("x-language", language.getValue())
                .exchange()
                .expectStatus()
                .isEqualTo(expectedStatus)
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(EXPECTED_RESULT, resource));
    }

    private static Stream<Arguments> executeTxtTemplateTestNoBody() {
        return Stream.of(
                Arguments.of(
                        SMSBODY,
                        TemplatesEnum.SMS_BODY.getTemplate(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PECSUBJECTREJECT,
                        TemplatesEnum.PEC_SUBJECT_REJECT.getTemplate(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PECSUBJECTCONFIRM,
                        TemplatesEnum.PEC_SUBJECT_CONFIRM.getTemplate(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PECSUBJECT,
                        TemplatesEnum.PEC_SUBJECT.getTemplate(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        EMAILSUBJECT,
                        TemplatesEnum.EMAIL_SUBJECT.getTemplate(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PECBODYREJECT,
                        TemplatesEnum.PEC_BODY_REJECT.getTemplate(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                )
        );
    }

    private static Stream<Arguments> executeTxtTemplateTest() {
        return Stream.of(
                Arguments.of(
                        NOTIFICATION_AARSUBJECT,
                        TemplatesEnum.NOTIFICATION_AAR_SUBJECT.getTemplate(),
                        new NotificationAARSubject(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_BODY_CONFIRM,
                        TemplatesEnum.PEC_BODY_CONFIRM.getTemplate(),
                        new Pecbody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PECBODY,
                        TemplatesEnum.PEC_BODY.getTemplate(),
                        new Pecbody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        EMAILBODY,
                        TemplatesEnum.EMAIL_BODY.getTemplate(),
                        new Emailbody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AAR_FOR_SMS,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_SMS.getTemplate(),
                        new NotificationAARForSMS(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AARFOR_PEC,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_PEC.getTemplate(),
                        new NotificationAARForPEC(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AARFOR_EMAIL,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL.getTemplate(),
                        new NotificationAARForEMAIL(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AAR_RADDALT,
                        TemplatesEnum.NOTIFICATION_AAR_RADDALT.getTemplate(),
                        new NotificationAARRADDalt(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AAR,
                        TemplatesEnum.NOTIFICATION_AAR.getTemplate(),
                        new NotificationAAR(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        EXPECTED_RESULT
                )
        );
    }

    private static Stream<Arguments> executePdfTemplateTest() {
        return Stream.of(
                Arguments.of(
                        ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT,
                        TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT.getTemplate(),
                        new AnalogDeliveryWorkflowFailureLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_CANCELLED_LEGAL_FACT,
                        TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT.getTemplate(),
                        new NotificationCancelledLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_VIEWED_LEGAL_FACT,
                        TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT.getTemplate(),
                        new NotificationViewedLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        LEGAL_FACT_MALFUNCTION,
                        TemplatesEnum.LEGAL_FACT_MALFUNCTION.getTemplate(),
                        new LegalFactMalfunction(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_RECEIVED_LEGAL_FACT,
                        TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate(),
                        new NotificationReceiverLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        PEC_DELIVERY_WORKFLOW_LEGAL_FACT,
                        TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT.getTemplate(),
                        new PecDeliveryWorkflowLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.ACCEPTED,
                        new byte[]{1, 2, 3}
                )
        );
    }

}
