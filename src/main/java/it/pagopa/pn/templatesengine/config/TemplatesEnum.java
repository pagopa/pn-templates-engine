package it.pagopa.pn.templatesengine.config;

import lombok.Getter;

@Getter
public enum TemplatesEnum {

    ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT("analogDeliveryWorkflowFailureLegalFact"),
    ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT("analogDeliveryWorkflowTimeoutLegalFact"),
    MALFUNCTION_LEGAL_FACT("malfunctionLegalFact"),
    NOTIFICATION_RECEIVED_LEGAL_FACT("notificationReceivedLegalFact"),
    NOTIFICATION_VIEWED_LEGAL_FACT("notificationViewedLegalFact"),
    NOTIFICATION_CANCELLED_LEGAL_FACT("notificationCancelledLegalFact"),
    NOTIFICATION_AAR("notificationAar"),
    NOTIFICATION_AAR_RADDALT("notificationAarRaddAlt"),
    NOTIFICATION_AAR_FOR_SUBJECT("notificationAarForSubject"),
    NOTIFICATION_AAR_FOR_EMAIL("notificationAarForEmail"),
    NOTIFICATION_AAR_FOR_PEC("notificationAarForPec"),
    NOTIFICATION_AAR_FOR_SMS("notificationAarForSms"),
    MAIL_VERIFICATION_CODE_BODY("mailVerificationCodeBody"),
    MAIL_VERIFICATION_CODE_SUBJECT("mailVerificationCodeSubject"),
    PEC_DELIVERY_WORKFLOW_LEGAL_FACT("pecDeliveryWorkflowLegalFact"),
    SMS_VERIFICATION_CODE_BODY("smsVerificationCodeBody"),
    PEC_VERIFICATION_CODE_BODY("pecVerificationCodeBody"),
    PEC_VERIFICATION_CODE_SUBJECT("pecVerificationCodeSubject"),
    PEC_VALIDATION_CONTACTS_REJECT_BODY("pecValidationContactsRejectBody"),
    PEC_VALIDATION_CONTACTS_REJECT_SUBJECT("pecValidationContactsRejectSubject"),
    PEC_VALIDATION_CONTACTS_SUCCESS_BODY("pecValidationContactsSuccessBody"),
    PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT("pecValidationContactsSuccessSubject"),
    NOTIFICATION_CCE_FOR_EMAIL("notificationCceForEmail"),
    NOTIFICATION_CCE_FOR_SUBJECT("notificationCceForSubject");

    private final String template;

    TemplatesEnum(String template) {
        this.template = template;
    }

}
