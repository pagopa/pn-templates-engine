package it.pagopa.pn.templatesengine.config;

import lombok.Getter;

@Getter
public enum TemplatesEnum {

    ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT("analogDeliveryWorkflowFailureLegalFact"),
    MALFUNCTION_LEGAL_FACT("malfunctionLegalFact"),
    NOTIFICATION_RECEIVED_LEGAL_FACT("notificationReceivedLegalFact"),
    NOTIFICATION_VIEWED_LEGAL_FACT("notificationViewedLegalFact"),
    NOTIFICATION_CANCELLED_LEGAL_FACT("notificationCancelledLegalFact"),
    NOTIFICATION_AAR("notificationAAR"),
    NOTIFICATION_AAR_RADDALT("notificationAAR_RADDalt"),
    NOTIFICATION_AAR_FOR_SUBJECT("notificationAARForSubject"),
    NOTIFICATION_AAR_FOR_EMAIL("notificationAARForEMAIL"),
    NOTIFICATION_AAR_FOR_PEC("notificationAARForPEC"),
    NOTIFICATION_AAR_FOR_SMS("notificationAARForSMS"),
    MAIL_VERIFICATION_CODE_BODY("mailVerificationCodeBody"),
    MAIL_VERIFICATION_CODE_SUBJECT("mailVerificationCodeSubject"),
    PEC_DELIVERY_WORKFLOW_LEGAL_FACT("pecDeliveryWorkflowLegalFact"),
    SMS_VERIFICATION_CODE_BODY("smsVerificationCodeBody"),
    PEC_VERIFICATION_CODE_BODY("pecVerificationCodeBody"),
    PEC_VERIFICATION_CODE_SUBJECT("pecVerificationCodeSubject"),
    PEC_VALIDATION_CONTACTS_REJECT_BODY("pecValidationContactsRejectBody"),
    PEC_VALIDATION_CONTACTS_REJECT_SUBJECT("pecValidationContactsRejectSubject"),
    PEC_VALIDATION_CONTACTS_SUCCESS_BODY("pecValidationContactsSuccessBody"),
    PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT("pecValidationContactsSuccessSubject");

    private final String template;

    TemplatesEnum(String template) {
        this.template = template;
    }

}
