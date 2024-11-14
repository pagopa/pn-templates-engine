package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.config.TemplatesEnum;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templates.engine.service.TemplateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(TemplateApiController.class)
class TemplateApiControllerTest {

    public static final String NOTIFICATION_RECEIVED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-received-legal-fact";
    public static final String PEC_DELIVERY_WORKFLOW_LEGAL_FACT = "/templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact";
    public static final String NOTIFICATION_CANCELLED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-cancelled-legal-fact";
    public static final String TEMPLATES_NOTIFICATION_AAR = "/templates-engine-private/v1/templates/notification-aar";
    public static final String NOTIFICATION_AAR_RADDALT = "/templates-engine-private/v1/templates/notificationAAR_RADDalt";
    public static final String ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT = "/templates-engine-private/v1/templates/analogDeliveryWorkflowFailureLegalFact";
    public static final String NOTIFICATION_AARFOR_EMAIL = "/templates-engine-private/v1/templates/notificationAARForEMAIL";
    public static final String NOTIFICATION_AARFOR_PEC = "/templates-engine-private/v1/templates/notificationAARForPEC";
    public static final String EMAILBODY = "/templates-engine-private/v1/templates/emailbody";
    public static final String PECBODY = "/templates-engine-private/v1/templates/pecbody";
    public static final String PECBODYCONFIRM = "/templates-engine-private/v1/templates/pecbodyconfirm";
    public static final String PECBODYREJECT = "/templates-engine-private/v1/templates/pecbodyreject";
    public static final String NOTIFICATION_VIEWED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-viewed-legal-fact";
    public static final String NOTIFICATION_AARSUBJECT = "/templates-engine-private/v1/templates/notification-aar-subject";
    public static final String EMAILSUBJECT = "/templates-engine-private/v1/templates/emailsubject";
    public static final String PECSUBJECT = "/templates-engine-private/v1/templates/pecsubject";
    public static final String PECSUBJECTCONFIRM = "/templates-engine-private/v1/templates/pecsubjectconfirm";
    public static final String PECSUBJECTREJECT = "/templates-engine-private/v1/templates/pecsubjectreject";
    public static final String LEGAL_FACT_MALFUNCTION = "/templates-engine-private/v1/templates/legal-fact-malfunction";

    @Autowired
    WebTestClient webTestClient;
    @MockBean
    TemplateService templateService;

    @Test
    void notificationViewedLegalFact_OK() {
        //ARRANGE
        NotificationViewedLegalFact request = new NotificationViewedLegalFact();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_VIEWED_LEGAL_FACT)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("x-language", "IT")
                .body(Mono.just(request), NotificationViewedLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assert responseData.length == pdfData.length;
                    assert responseData[0] == pdfData[0];
                    assert responseData[1] == pdfData[1];
                    assert responseData[2] == pdfData[2];
                });
    }

    @Test
    void legalFactMalfunction() {
        //ARRANGE
        LegalFactMalfunction request = new LegalFactMalfunction();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.LEGAL_FACT_MALFUNCTION.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(pdfData));
        //ACT
        webTestClient.put()
                .uri(LEGAL_FACT_MALFUNCTION)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("x-language", "IT")
                .body(Mono.just(request), LegalFactMalfunction.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assert responseData.length == pdfData.length;
                    assert responseData[0] == pdfData[0];
                    assert responseData[1] == pdfData[1];
                    assert responseData[2] == pdfData[2];
                });
    }

    @Test
    void notificationViewedLegalFact_Language_Not_Exist() {
        //ARRANGE
        NotificationViewedLegalFact request = new NotificationViewedLegalFact();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_VIEWED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "MD")
                .body(Mono.just(request), NotificationViewedLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationReceivedLegalFact_OK() {
        //ARRANGE
        NotificationReceiverLegalFact request = new NotificationReceiverLegalFact();
        request.setDigest("10");
        request.setSubject("SUBJECT_TEST");

        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_RECEIVED_LEGAL_FACT)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("x-language", "IT")
                .body(Mono.just(request), NotificationReceiverLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assert responseData.length == pdfData.length;
                    assert responseData[0] == pdfData[0];
                    assert responseData[1] == pdfData[1];
                    assert responseData[2] == pdfData[2];
                });
    }

    @Test
    void notificationReceivedLegalFact_Language_Not_Exist() {
        //ARRANGE
        NotificationReceiverLegalFact request = new NotificationReceiverLegalFact();
        request.setDigest("10");
        request.setSubject("SUBJECT_TEST");

        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_RECEIVED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "MD")
                .body(Mono.just(request), NotificationReceiverLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecDeliveryWorkflowLegalFact_OK() {
        //ARRANGE
        PecDeliveryWorkflowLegalFact request = new PecDeliveryWorkflowLegalFact();
        request.setIun("IUN_TEST");
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(PEC_DELIVERY_WORKFLOW_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "DE")
                .body(Mono.just(request), PecDeliveryWorkflowLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assert responseData.length == pdfData.length;
                    assert responseData[0] == pdfData[0];
                    assert responseData[1] == pdfData[1];
                    assert responseData[2] == pdfData[2];
                });
    }

    @Test
    void pecDeliveryWorkflowLegalFact_Language_Not_Exist() {
        //ARRANGE
        PecDeliveryWorkflowLegalFact request = new PecDeliveryWorkflowLegalFact();
        request.setIun("IUN_TEST");
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.empty());

        //ACT
        webTestClient.put()
                .uri(PEC_DELIVERY_WORKFLOW_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "MD")
                .body(Mono.just(request), PecDeliveryWorkflowLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationCancelledLegalFact_OK() {
        //ARRANGE
        NotificationCancelledLegalFact request = new NotificationCancelledLegalFact();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT.getTemplate(), LanguageEnum.FR, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_CANCELLED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "FR")
                .body(Mono.just(request), NotificationCancelledLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assert responseData.length == pdfData.length;
                    assert responseData[0] == pdfData[0];
                    assert responseData[1] == pdfData[1];
                    assert responseData[2] == pdfData[2];
                });
    }

    @Test
    void notificationCancelledLegalFact_Language_Not_Exist() {
        //ARRANGE
        NotificationCancelledLegalFact request = new NotificationCancelledLegalFact();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_CANCELLED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "GR")
                .body(Mono.just(request), NotificationCancelledLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAAR_OK() {
        //ARRANGE
        String expectedResult = "notificationAAR_OK";
        NotificationAAR request = new NotificationAAR();
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(TEMPLATES_NOTIFICATION_AAR)
                .accept(MediaType.ALL)
                .header("x-language", "DE")
                .body(Mono.just(request), NotificationAAR.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void notificationAAR_Language_Not_Exist() {
        //ARRANGE
        NotificationAAR request = new NotificationAAR();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(TEMPLATES_NOTIFICATION_AAR)
                .accept(MediaType.ALL)
                .header("x-language", "BT")
                .body(Mono.just(request), NotificationAAR.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARRADDalt_OK() {
        //ARRANGE
        String expectedResult = "notificationAARRADDalt_OK";
        NotificationAARRADDalt request = new NotificationAARRADDalt();
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR_RADDALT.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AAR_RADDALT)
                .accept(MediaType.ALL)
                .header("x-language", "DE")
                .body(Mono.just(request), NotificationAARRADDalt.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void notificationAARRADDalt_Language_Not_Exist() {
        //ARRANGE
        String expectedResult = "notificationAARRADDalt_Language_Not_Exist";
        NotificationAARRADDalt request = new NotificationAARRADDalt();
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR_RADDALT.getTemplate(), LanguageEnum.DE, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AAR_RADDALT)
                .accept(MediaType.ALL)
                .header("x-language", "DA")
                .body(Mono.just(request), NotificationAARRADDalt.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void analogDeliveryWorkflowFailureLegalFact_OK() {
        //ARRANGE
        AnalogDeliveryWorkflowFailureLegalFact request = new AnalogDeliveryWorkflowFailureLegalFact();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT.getTemplate(), LanguageEnum.SL, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "SL")
                .body(Mono.just(request), AnalogDeliveryWorkflowFailureLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(ByteArrayResource.class)
                .value(resource -> {
                    byte[] responseData = resource.getByteArray();
                    assert responseData.length == pdfData.length;
                    assert responseData[0] == pdfData[0];
                    assert responseData[1] == pdfData[1];
                    assert responseData[2] == pdfData[2];
                });
    }

    @Test
    void analogDeliveryWorkflowFailureLegalFact_Language_Not_Exist() {
        //ARRANGE
        AnalogDeliveryWorkflowFailureLegalFact request = new AnalogDeliveryWorkflowFailureLegalFact();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT.getTemplate(), LanguageEnum.SL, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("x-language", "SB")
                .body(Mono.just(request), AnalogDeliveryWorkflowFailureLegalFact.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARForEMAIL_OK() {
        //ARRANGE
        String expectedResult = "notificationAARForEMAIL_OK";
        NotificationAARForEMAIL request = new NotificationAARForEMAIL();
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL.getTemplate(), LanguageEnum.FR, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_EMAIL)
                .accept(MediaType.ALL)
                .header("x-language", "FR")
                .body(Mono.just(request), NotificationAARForEMAIL.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void notificationAARForEMAIL_Language_Not_Exist() {
        //ARRANGE
        NotificationAARForEMAIL request = new NotificationAARForEMAIL();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL.getTemplate(), LanguageEnum.SL, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_EMAIL)
                .accept(MediaType.ALL)
                .header("x-language", "sT")
                .body(Mono.just(request), NotificationAARForEMAIL.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARForPEC_OK() {
        //ARRANGE
        String expectedResult = "notificationAARForPEC_OK";
        NotificationAARForPEC request = new NotificationAARForPEC();
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_PEC.getTemplate(), LanguageEnum.SL, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_PEC)
                .accept(MediaType.ALL)
                .header("x-language", "SL")
                .body(Mono.just(request), NotificationAARForPEC.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void notificationAARForPEC_Language_Not_Exist() {
        //ARRANGE
        NotificationAARForPEC request = new NotificationAARForPEC();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate(), LanguageEnum.FR, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_PEC)
                .accept(MediaType.ALL)
                .header("x-language", "WE")
                .body(Mono.just(request), NotificationAARForPEC.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void emailbody_Language_Not_Exist() {
        //ARRANGE
        Emailbody request = new Emailbody();
        byte[] pdfData = new byte[]{1, 2, 3};
        Mockito.when(templateService.executePdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate(), LanguageEnum.FR, request))
                .thenReturn(Mono.just(pdfData));

        //ACT
        webTestClient.put()
                .uri(EMAILBODY)
                .accept(MediaType.ALL)
                .header("x-language", "DR")
                .body(Mono.just(request), Emailbody.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void emailbody_OK() {
        //ARRANGE
        Emailbody request = new Emailbody();
        String expectedResult = "pecbody_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.EMAIL_BODY.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(EMAILBODY)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .body(Mono.just(request), Emailbody.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecbody_OK() {
        //ARRANGE
        Pecbody request = new Pecbody();
        String expectedResult = "pecbody_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.PEC_BODY.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(PECBODY)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .body(Mono.just(request), Pecbody.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecbody_Language_Not_Exist() {
        //ARRANGE
        Pecbody request = new Pecbody();

        //ACT
        webTestClient.put()
                .uri(PECBODY)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .body(Mono.just(request), Pecbody.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecbodyconfirm_OK() {
        //ARRANGE
        String expectedResult = "pecbodyconfirm_OK";
        Pecbody request = new Pecbody();
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.PEC_BODY_CONFIRM.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(PECBODYCONFIRM)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .body(Mono.just(request), Pecbody.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecbodyconfirm_Language_Not_Exist() {
        //ARRANGE
        Pecbody request = new Pecbody();

        //ACT
        webTestClient.put()
                .uri(PECBODYCONFIRM)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .body(Mono.just(request), Pecbody.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecbodyreject_OK() {
        //ARRANGE
        String expectedResult = "pecbodyreject_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.PEC_BODY_REJECT.getTemplate(), LanguageEnum.IT))
                .thenReturn(Mono.just(expectedResult));

        // ACT
        webTestClient.put()
                .uri(PECBODYREJECT)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecbodyreject_Language_Not_Exist() {
        //ARRANGE AND  //ACT
        webTestClient.put()
                .uri(PECBODYREJECT)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARSubject_OK() {
        //ARRANGE
        NotificationAAR request = new NotificationAAR();
        String expectedResult = "notificationAARSubject_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR_SUBJECT.getTemplate(), LanguageEnum.IT, request))
                .thenReturn(Mono.just(expectedResult));

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AARSUBJECT)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("x-language", "IT")
                .body(Mono.just(request), NotificationAAR.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .isEqualTo(expectedResult);
    }

    @Test
    void notificationAARSubject_Language_Not_Exist() {
        //ARRANGE
        NotificationAAR request = new NotificationAAR();

        //ACT
        webTestClient.put()
                .uri(NOTIFICATION_AARSUBJECT)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .body(Mono.just(request), NotificationAAR.class)
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void emailsubject_OK() {
        //ARRANGE
        String expectedResult = "emailsubject_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.EMAIL_SUBJECT.getTemplate(), LanguageEnum.IT))
                .thenReturn(Mono.just(expectedResult));

        // ACT
        webTestClient.put()
                .uri(EMAILSUBJECT)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .isEqualTo(expectedResult);
    }

    @Test
    void emailsubject_Language_Not_Exist() {
        //ARRANGE - ACT
        webTestClient.put()
                .uri(EMAILSUBJECT)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecsubject_OK() {
        //ARRANGE
        String expectedResult = "pecsubject_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.PEC_SUBJECT.getTemplate(), LanguageEnum.IT))
                .thenReturn(Mono.just(expectedResult));

        // ACT
        webTestClient.put()
                .uri(PECSUBJECT)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecsubject_Language_Not_Exist() {
        //ARRANGE - ACT
        webTestClient.put()
                .uri(PECSUBJECT)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecsubjectconfirm_OK() {
        //ARRANGE
        String expectedResult = "pecsubject_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.PEC_SUBJECT_CONFIRM.getTemplate(), LanguageEnum.IT))
                .thenReturn(Mono.just(expectedResult));

        // ACT
        webTestClient.put()
                .uri(PECSUBJECTCONFIRM)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecsubjectconfirm_Language_Not_Exist() {
        //ARRANGE - ACT
        webTestClient.put()
                .uri(PECSUBJECTCONFIRM)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }


    @Test
    void pecsubjectreject_OK() {
        //ARRANGE
        String expectedResult = "pecsubjectreject_OK";
        Mockito.when(templateService.executeTextTemplate(TemplatesEnum.PEC_SUBJECT_REJECT.getTemplate(), LanguageEnum.IT))
                .thenReturn(Mono.just(expectedResult));

        // ACT
        webTestClient.put()
                .uri(PECSUBJECTREJECT)
                .accept(MediaType.ALL)
                .header("x-language", "IT")
                .exchange()

                //ASSERT

                .expectStatus()
                .isAccepted()
                .expectBody(String.class)
                .value(resource -> Assertions.assertEquals(expectedResult, resource));
    }

    @Test
    void pecsubjectreject_Language_Not_Exist() {
        //ARRANGE - ACT
        webTestClient.put()
                .uri(PECSUBJECTREJECT)
                .accept(MediaType.ALL)
                .header("x-language", "UR")
                .exchange()

                //ASSERT

                .expectStatus()
                .isEqualTo(400);
    }

}