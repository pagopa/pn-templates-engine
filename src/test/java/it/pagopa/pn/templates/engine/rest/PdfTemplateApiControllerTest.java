package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(PdfTemplateApiController.class)
class PdfTemplateApiControllerTest {

    public static final String NOTIFICATION_RECEIVED_LEGAL_FACT = "/templates-engine-private/v1/templates/notification-received-legal-fact";
    public static final String PEC_DELIVERY_WORKFLOW_LEGAL_FACT = "/templates-engine-private/v1/templates/pec-delivery-workflow-legal-fact";
    public static final String PDF_LEGAL_FACT = "/templates-engine-private/v1/templates/pdf-legal-fact";
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

    @Autowired
    WebTestClient webTestClient;

    @Test
    void notificationViewedLegalFact_OK() {
        NotificationViewedLegalFact request = new NotificationViewedLegalFact();
        webTestClient.put()
                .uri(NOTIFICATION_VIEWED_LEGAL_FACT)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("X-Language", "IT")
                .body(Mono.just(request), NotificationViewedLegalFact.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationViewedLegalFact_Language_Not_Exist() {
        NotificationViewedLegalFact request = new NotificationViewedLegalFact();
        webTestClient.put()
                .uri(NOTIFICATION_VIEWED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "MD")
                .body(Mono.just(request), NotificationViewedLegalFact.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationReceivedLegalFact_OK() {
        NotificationReceiverLegalFact request = new NotificationReceiverLegalFact();
        request.setDigest("10");
        request.setSubject("SUBJECT_TEST");
        webTestClient.put()
                .uri(NOTIFICATION_RECEIVED_LEGAL_FACT)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, "application/json")
                .header("X-Language", "IT")
                .body(Mono.just(request), NotificationReceiverLegalFact.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationReceivedLegalFact_Language_Not_Exist() {
        NotificationReceiverLegalFact request = new NotificationReceiverLegalFact();
        request.setDigest("10");
        request.setSubject("SUBJECT_TEST");
        webTestClient.put()
                .uri(NOTIFICATION_RECEIVED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "MD")
                .body(Mono.just(request), NotificationReceiverLegalFact.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecDeliveryWorkflowLegalFact_OK() {
        PecDeliveryWorkflowLegalFact request = new PecDeliveryWorkflowLegalFact();
        request.setIun("IUN_TEST");
        webTestClient.put()
                .uri(PEC_DELIVERY_WORKFLOW_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "DE")
                .body(Mono.just(request), PecDeliveryWorkflowLegalFact.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void pecDeliveryWorkflowLegalFact_Language_Not_Exist() {
        PecDeliveryWorkflowLegalFact request = new PecDeliveryWorkflowLegalFact();
        request.setIun("IUN_TEST");
        webTestClient.put()
                .uri(PEC_DELIVERY_WORKFLOW_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "MD")
                .body(Mono.just(request), PecDeliveryWorkflowLegalFact.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pdfLegalFact_Language_Not_Exist() {
        PdfLegalFact request = new PdfLegalFact();
        webTestClient.put()
                .uri(PDF_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "MD")
                .body(Mono.just(request), PdfLegalFact.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pdfLegalFact_OK() {
        PdfLegalFact request = new PdfLegalFact();
        webTestClient.put()
                .uri(PDF_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "SL")
                .body(Mono.just(request), PdfLegalFact.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationCancelledLegalFact_OK() {
        NotificationCancelledLegalFact request = new NotificationCancelledLegalFact();
        webTestClient.put()
                .uri(NOTIFICATION_CANCELLED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "FR")
                .body(Mono.just(request), NotificationCancelledLegalFact.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationCancelledLegalFact_Language_Not_Exist() {
        NotificationCancelledLegalFact request = new NotificationCancelledLegalFact();
        webTestClient.put()
                .uri(NOTIFICATION_CANCELLED_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "GR")
                .body(Mono.just(request), NotificationCancelledLegalFact.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAAR_OK() {
        NotificationAAR request = new NotificationAAR();
        webTestClient.put()
                .uri(TEMPLATES_NOTIFICATION_AAR)
                .accept(MediaType.ALL)
                .header("X-Language", "DE")
                .body(Mono.just(request), NotificationAAR.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationAAR_Language_Not_Exist() {
        NotificationAAR request = new NotificationAAR();
        webTestClient.put()
                .uri(TEMPLATES_NOTIFICATION_AAR)
                .accept(MediaType.ALL)
                .header("X-Language", "BT")
                .body(Mono.just(request), NotificationAAR.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARRADDalt_OK() {
        NotificationAARRADDalt request = new NotificationAARRADDalt();
        webTestClient.put()
                .uri(NOTIFICATION_AAR_RADDALT)
                .accept(MediaType.ALL)
                .header("X-Language", "DE")
                .body(Mono.just(request), NotificationAARRADDalt.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationAARRADDalt_Language_Not_Exist() {
        NotificationAARRADDalt request = new NotificationAARRADDalt();
        webTestClient.put()
                .uri(NOTIFICATION_AAR_RADDALT)
                .accept(MediaType.ALL)
                .header("X-Language", "DA")
                .body(Mono.just(request), NotificationAARRADDalt.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void analogDeliveryWorkflowFailureLegalFact_OK() {
        AnalogDeliveryWorkflowFailureLegalFact request = new AnalogDeliveryWorkflowFailureLegalFact();
        webTestClient.put()
                .uri(ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "SL")
                .body(Mono.just(request), AnalogDeliveryWorkflowFailureLegalFact.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void analogDeliveryWorkflowFailureLegalFact_Language_Not_Exist() {
        AnalogDeliveryWorkflowFailureLegalFact request = new AnalogDeliveryWorkflowFailureLegalFact();
        webTestClient.put()
                .uri(ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT)
                .accept(MediaType.ALL)
                .header("X-Language", "SB")
                .body(Mono.just(request), AnalogDeliveryWorkflowFailureLegalFact.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARForEMAIL_OK() {
        NotificationAARForEMAIL request = new NotificationAARForEMAIL();
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_EMAIL)
                .accept(MediaType.ALL)
                .header("X-Language", "FR")
                .body(Mono.just(request), NotificationAARForEMAIL.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationAARForEMAIL_Language_Not_Exist() {
        NotificationAARForEMAIL request = new NotificationAARForEMAIL();
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_EMAIL)
                .accept(MediaType.ALL)
                .header("X-Language", "sT")
                .body(Mono.just(request), NotificationAARForEMAIL.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void notificationAARForPEC_OK() {
        NotificationAARForPEC request = new NotificationAARForPEC();
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_PEC)
                .accept(MediaType.ALL)
                .header("X-Language", "SL")
                .body(Mono.just(request), NotificationAARForPEC.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void notificationAARForPEC_Language_Not_Exist() {
        NotificationAARForPEC request = new NotificationAARForPEC();
        webTestClient.put()
                .uri(NOTIFICATION_AARFOR_PEC)
                .accept(MediaType.ALL)
                .header("X-Language", "WE")
                .body(Mono.just(request), NotificationAARForPEC.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void emailbody_Language_Not_Exist() {
        Emailbody request = new Emailbody();
        webTestClient.put()
                .uri(EMAILBODY)
                .accept(MediaType.ALL)
                .header("X-Language", "DR")
                .body(Mono.just(request), Emailbody.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void emailbody_OK() {
        Emailbody request = new Emailbody();
        webTestClient.put()
                .uri(EMAILBODY)
                .accept(MediaType.ALL)
                .header("X-Language", "IT")
                .body(Mono.just(request), Emailbody.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void pecbody_OK() {
        Pecbody request = new Pecbody();
        webTestClient.put()
                .uri(PECBODY)
                .accept(MediaType.ALL)
                .header("X-Language", "IT")
                .body(Mono.just(request), Pecbody.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void pecbody_Language_Not_Exist() {
        Pecbody request = new Pecbody();
        webTestClient.put()
                .uri(PECBODY)
                .accept(MediaType.ALL)
                .header("X-Language", "UR")
                .body(Mono.just(request), Pecbody.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecbodyconfirm_OK() {
        Pecbody request = new Pecbody();
        webTestClient.put()
                .uri(PECBODYCONFIRM)
                .accept(MediaType.ALL)
                .header("X-Language", "IT")
                .body(Mono.just(request), Pecbody.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void pecbodyconfirm_Language_Not_Exist() {
        Pecbody request = new Pecbody();
        webTestClient.put()
                .uri(PECBODYCONFIRM)
                .accept(MediaType.ALL)
                .header("X-Language", "UR")
                .body(Mono.just(request), Pecbody.class)
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

    @Test
    void pecbodyreject_OK() {
        webTestClient.put()
                .uri(PECBODYREJECT)
                .accept(MediaType.ALL)
                .header("X-Language", "IT")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void pecbodyreject_Language_Not_Exist() {
        webTestClient.put()
                .uri(PECBODYREJECT)
                .accept(MediaType.ALL)
                .header("X-Language", "UR")
                .exchange()
                .expectStatus()
                .isEqualTo(400);
    }

}