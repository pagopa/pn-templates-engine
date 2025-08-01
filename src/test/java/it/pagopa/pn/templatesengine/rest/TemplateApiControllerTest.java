package it.pagopa.pn.templatesengine.rest;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templatesengine.resolver.TemplateValueResolver;
import it.pagopa.pn.templatesengine.service.TemplateService;
import it.pagopa.pn.templatesengine.utils.TemplatesExampleParameters;
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

@WebFluxTest(TemplateApiController.class)
public class TemplateApiControllerTest {

    public static final String EXPECTED_RESULT = "executeTxtTemplateTest";

    public static final String NOTIFICATION_RECEIVED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-received-legal-fact";
    public static final String PEC_DELIVERY_WORKFLOW_LEGAL_FACT = "/templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact";
    public static final String NOTIFICATION_CANCELLED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-cancelled-legal-fact";
    public static final String NOTIFICATION_AAR = "/templates-engine-private/v1/templates/notification-aar";
    public static final String NOTIFICATION_AAR_RADD_ALT = "/templates-engine-private/v1/templates/notification-aar-radd-alt";
    public static final String ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT = "/templates-engine-private/v1/templates/analog-delivery-workflow-failure-legal-fact";
    public static final String NOTIFICATION_AAR_FOR_EMAIL = "/templates-engine-private/v1/templates/notification-aar-for-email";
    public static final String NOTIFICATION_AAR_FOR_PEC = "/templates-engine-private/v1/templates/notification-aar-for-pec";
    public static final String MAIL_VERIFICATION_CODE_BODY = "/templates-engine-private/v1/templates/mail-verification-code-body";
    public static final String PEC_VERIFICATION_CODE_BODY = "/templates-engine-private/v1/templates/pec-verification-code-body";
    public static final String PEC_VALIDATION_CONTACTS_SUCCESS_BODY = "/templates-engine-private/v1/templates/pec-validation-contacts-success-body";
    public static final String PEC_VALIDATION_CONTACTS_REJECT_BODY = "/templates-engine-private/v1/templates/pec-validation-contacts-reject-body";
    public static final String NOTIFICATION_VIEWED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-viewed-legal-fact";
    public static final String NOTIFICATION_AAR_FOR_SUBJECT = "/templates-engine-private/v1/templates/notification-aar-for-subject";
    public static final String MAIL_VERIFICATION_CODE_SUBJECT = "/templates-engine-private/v1/templates/mail-verification-code-subject";
    public static final String PEC_VERIFICATION_CODE_SUBJECT = "/templates-engine-private/v1/templates/pec-verification-code-subject";
    public static final String PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT = "/templates-engine-private/v1/templates/pec-validation-contacts-success-subject";
    public static final String PEC_VALIDATION_CONTACTS_REJECT_SUBJECT = "/templates-engine-private/v1/templates/pec-validation-contacts-reject-subject";
    public static final String MALFUNCTION_LEGAL_FACT = "/templates-engine-private/v1/templates/malfunction-legal-fact";
    public static final String NOTIFICATION_AAR_FOR_SMS = "/templates-engine-private/v1/templates/notification-aar-for-sms";
    public static final String SMS_VERIFICATION_CODE_BODY = "/templates-engine-private/v1/templates/sms-verification-code-body";

    @Autowired
    WebTestClient webTestClient;
    @MockBean
    TemplateService templateService;
    @MockBean
    TemplateValueResolver templateValueResolver;

    @ParameterizedTest
    @MethodSource("executePdfTemplateTest")
    void testPdfTemplateWebClient(
            String testName,
            TemplatesEnum template,
            Object request,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus,
            byte[] expectedData
    ) {
        // Arrange
        Mockito.when(templateService.executePdfTemplate(Mockito.eq(template), Mockito.eq(language), Mockito.any(Mono.class)))
                .thenReturn(Mono.just(expectedData));
        Mockito.when(templateValueResolver.resolve(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Mono.just("test_resolved"));

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
            TemplatesEnum template,
            Object request,
            LanguageEnum language,
            MediaType mediaType,
            HttpStatus expectedStatus,
            String expectedData
    ) {
        // Arrange
        Mockito.when(templateService.executeTextTemplate(Mockito.eq(template), Mockito.eq(language), Mockito.any(Mono.class)))
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
            TemplatesEnum templateName,
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
                        SMS_VERIFICATION_CODE_BODY,
                        TemplatesEnum.SMS_VERIFICATION_CODE_BODY,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_VALIDATION_CONTACTS_REJECT_SUBJECT,
                        TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_SUBJECT,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT,
                        TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_VERIFICATION_CODE_SUBJECT,
                        TemplatesEnum.PEC_VERIFICATION_CODE_SUBJECT,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        MAIL_VERIFICATION_CODE_SUBJECT,
                        TemplatesEnum.MAIL_VERIFICATION_CODE_SUBJECT,
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                )
        );
    }

    private static Stream<Arguments> executeTxtTemplateTest() {
        return Stream.of(
                Arguments.of(
                        NOTIFICATION_AAR_FOR_SUBJECT,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_SUBJECT,
                        new NotificationAarForSubject(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_VERIFICATION_CODE_BODY,
                        TemplatesEnum.PEC_VERIFICATION_CODE_BODY,
                        new PecVerificationCodeBody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        MAIL_VERIFICATION_CODE_BODY,
                        TemplatesEnum.MAIL_VERIFICATION_CODE_BODY,
                        new MailVerificationCodeBody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AAR_FOR_SMS,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_SMS,
                        new NotificationAarForSms(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AAR_FOR_PEC,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_PEC,
                        new NotificationAarForPec(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        NOTIFICATION_AAR_FOR_EMAIL,
                        TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL,
                        new NotificationAarForEmail(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_VALIDATION_CONTACTS_SUCCESS_BODY,
                        TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_BODY,
                        new PecValidationContactsBody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                ),
                Arguments.of(
                        PEC_VALIDATION_CONTACTS_REJECT_BODY,
                        TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_BODY,
                        new PecValidationContactsBody(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        EXPECTED_RESULT
                )
        );
    }

    private static Stream<Arguments> executePdfTemplateTest() {
        return Stream.of(
                Arguments.of(
                        ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT,
                        TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT,
                        Mono.just(new AnalogDeliveryWorkflowFailureLegalFact()),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_CANCELLED_LEGAL_FACT,
                        TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT,
                        Mono.just(new NotificationCancelledLegalFact()),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_VIEWED_LEGAL_FACT,
                        TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT,
                        Mono.just(new NotificationViewedLegalFact()),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        MALFUNCTION_LEGAL_FACT,
                        TemplatesEnum.MALFUNCTION_LEGAL_FACT,
                        new MalfunctionLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_RECEIVED_LEGAL_FACT,
                        TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT,
                        new NotificationReceivedLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        PEC_DELIVERY_WORKFLOW_LEGAL_FACT,
                        TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT,
                        new PecDeliveryWorkflowLegalFact(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_AAR_RADD_ALT,
                        TemplatesEnum.NOTIFICATION_AAR_RADDALT,
                        TemplatesExampleParameters.getNotificationAarRaddAlt(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                ),
                Arguments.of(
                        NOTIFICATION_AAR,
                        TemplatesEnum.NOTIFICATION_AAR,
                        TemplatesExampleParameters.getNotificationAar(),
                        LanguageEnum.IT,
                        MediaType.APPLICATION_JSON,
                        HttpStatus.OK,
                        new byte[]{1, 2, 3}
                )
        );
    }

}
