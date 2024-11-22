package it.pagopa.pn.templatesengine.config;

import lombok.Getter;

@Getter
public enum TemplatesEnum {

    ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT("analogDeliveryWorkflowFailureLegalFact"),
    NOTIFICATION_RECEIVED_LEGAL_FACT("notificationReceivedLegalFact"),
    MALFUNCTION_LEGAL_FACT("malfunctionLegalFact"),
    NOTIFICATION_VIEWED_LEGAL_FACT("notificationViewedLegalFact"),
    NOTIFICATION_AAR_FOR_EMAIL("notificationAARForEMAIL"),
    NOTIFICATION_AAR_FOR_PEC("notificationAARForPEC"),
    NOTIFICATION_AAR_FOR_SMS("notificationAARForSMS"),
    NOTIFICATION_AAR_SUBJECT("notificationAARSubject"),
    NOTIFICATION_CANCELLED_LEGAL_FACT("notificationCancelledLegalFact"),
    NOTIFICATION_AAR("notificationAAR"),
    NOTIFICATION_AAR_RADDALT("notificationAAR_RADDalt"),
    PEC_DELIVERY_WORKFLOW_LEGAL_FACT("pecDeliveryWorkflowLegalFact"),
    MAIL_VERIFICATION_CODE_BODY("mailVerificationCodeBody"),
    MAIL_VERIFICATION_CODE_SUBJECT("mailVerificationCodeSubject"),
    PEC_VALIDATION_CONTACT_SUCCESS_BODY("pecValidationContactsSuccessBody"),
    PEC_VALIDATION_CONTACT_SUCCESS_SUBJECT("pecValidationContactsSuccessSubject"),
    PEC_VALIDATION_CONTACTS_REJECT_BODY("pecValidationContactsRejectBody"),
    PEC_VALIDATION_CONTACTS_REJECT_SUBJECT("pecValidationContactsRejectSubject"),
    SMS_VERIFICATION_CODE_BODY("smsVerificationCodeBody"),
    PEC_VERIFICATION_CODE_SUBJECT("pecVerificationCodeSubject"),
    PEC_PEC_VERIFICATION_CODE_SUBJECT_REJECT("pecsubjectreject");

    private final String template;

    TemplatesEnum(String template) {
        this.template = template;
    }

}
