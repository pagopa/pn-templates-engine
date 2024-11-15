package it.pagopa.pn.templatesengine.config;

import lombok.Getter;

@Getter
public enum TemplatesEnum {
    NOTIFICATION_RECEIVED_LEGAL_FACT("notificationReceivedLegalFact"),
    PEC_DELIVERY_WORKFLOW_LEGAL_FACT("pecDeliveryWorkflowLegalFact"),
    NOTIFICATION_VIEWED_LEGAL_FACT("notificationViewedLegalFact"),
    LEGAL_FACT_MALFUNCTION("legalFactMalfunction"),
    NOTIFICATION_CANCELLED_LEGAL_FACT("notificationCancelledLegalFact"),
    NOTIFICATION_AAR("notificationAAR"),
    NOTIFICATION_AAR_RADDALT("notificationAAR_RADDalt"),
    ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT("analogDeliveryWorkflowFailureLegalFact"),
    NOTIFICATION_AAR_FOR_EMAIL("notificationAARForEMAIL"),
    NOTIFICATION_AAR_FOR_PEC("notificationAARForPEC"),
    NOTIFICATION_AAR_FOR_SMS("notificationAARForSMS"),
    EMAIL_BODY("emailbody"),
    PEC_BODY("pecbody"),
    PEC_BODY_CONFIRM("pecbodyconfirm"),
    PEC_BODY_REJECT("pecbodyreject"),
    SMS_BODY("smsbody"),

    NOTIFICATION_AAR_SUBJECT("notificationAARSubject"),
    EMAIL_SUBJECT("emailsubject"),
    PEC_SUBJECT("pecsubject"),
    PEC_SUBJECT_CONFIRM("pecsubjectconfirm"),
    PEC_SUBJECT_REJECT("pecsubjectreject");

    private final String template;

    TemplatesEnum(String template) {
        this.template = template;
    }

}
