package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import static it.pagopa.pn.templatesengine.utils.QrCodeUtils.getQrCodeQuickAccessUrlAarDetail;

@SpringBootTest
public class DocumentGeneratorTest {
    private static final String TEST_DIR_NAME = "target" + File.separator + "generated-test-documents";
    private static final Path TEST_DIR_PATH = Paths.get(TEST_DIR_NAME);
    public static final String CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST = "https://cittadini.notifichedigitali.it/?aar=V1JXSi1QTUpZLUhMUlUtMjAyNTAxLU4tMV9QRi01MDI0Y2Q1Yi00NTE2LTQ5OWMtOTdiNi0wMDY5YmM4ODI1ODlfZGE4NjVmOTItNDljZS00NzYxLTkwM2EtYTg3MDEyZWM1N2Nm";
    @Autowired
    TemplateService templateService;

    @BeforeEach
    public void setup() throws IOException {
        // create target test folder, if not exists
        if (Files.notExists(TEST_DIR_PATH)) {
            Files.createDirectory(TEST_DIR_PATH);
        }
    }

    /** PDF **/
    @Test
    void generate_notificationReceivedLegalFact() {
        var template = TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var recipient = new NotificationReceivedRecipient()
                .denomination("Galileo Bruno")
                .digitalDomicile(new NotificationReceivedDigitalDomicile().address("test@dominioPec.it"))
                .taxId("CDCFSC11R99X001Z")
                .physicalAddressAndDenomination("TEST_PhysicalAddressAndDenomination");
        var notification = new NotificationReceivedNotification()
                .iun("TEST")
                .recipients(Collections.singletonList(recipient))
                .sender(new NotificationReceivedSender()
                        .paDenomination("TEST_paDenomination")
                        .paTaxId("TEST_paTaxId"));
        var model = new NotificationReceivedLegalFact()
                .notification(notification)
                .digests(new ArrayList<>())
                .sendDate("TEST_sendDate")
                .subject("TEST_subject");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_pecDeliveryWorkflowLegalFact() {
        var template = TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("TEST_denomination")
                .taxId("TEST_taxId")
                .address("TEST_address")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("TEST_responseDate")
                .ok(true);
        var model = new PecDeliveryWorkflowLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowStatus("TEST_endWorkflowStatus")
                .deliveries(Collections.singletonList(delivery));
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationViewedLegalFact() {
        var template = TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var recipient = new NotificationViewedRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        var delegate = new NotificationViewedDelegate()
                .denomination("Mario Rossi")
                .taxId("MRRSSC11R99X001Z");
        var model = new NotificationViewedLegalFact()
                .iun("TEST_iun")
                .when("TEST_when")
                .recipient(recipient)
                .delegate(delegate);
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_malfunctionLegalFact() {
        var template = TemplatesEnum.MALFUNCTION_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new MalfunctionLegalFact()
                .startDate("TEST_startDate")
                .timeReferenceStartDate("TEST_timeReferenceStartDate")
                .endDate("TEST_endDate")
                .timeReferenceEndDate("TEST_timeReferenceEndDate")
                .htmlDescription("<p>Sample <b>TEST_description</b></p>");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationCancelledLegalFact() {
        var template = TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var recipient = new NotificationCancelledRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        var sender = new NotificationCancelledSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new NotificationCancelledNotification()
                .iun("TEST")
                .recipients(Collections.singletonList(recipient))
                .sender(sender);
        var model = new NotificationCancelledLegalFact()
                .notificationCancelledDate("TEST_startDate")
                .notification(notification);
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationAar() {
        var template = TemplatesEnum.NOTIFICATION_AAR;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var sender = new AarSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarNotification()
                .iun("TEST_iun")
                .sender(sender)
                .subject("notification Titolo di 134 caratteri massimi spazi compresi. Aid olotielit, sed eiusmod tempora incidunt ue et et dolore magna aliqua aliqua aliqua");
        var recepient = new AarRecipient()
                .recipientType("PF")
                .taxId("CDCFSC11R99X001Z");
        var model = new NotificationAar()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recepient)
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationAarRaddAlt() {
        var template = TemplatesEnum.NOTIFICATION_AAR_RADDALT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var sender = new AarRaddAltSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarRaddAltNotification()
                .iun("TEST_iun")
                .sender(sender)
                .subject("notification Titolo di 134 caratteri massimi spazi compresi. Aid olotielit, sed eiusmod tempora incidunt ue et et dolore magna aliqua aliqua aliqua");
        var recepient = new AarRaddAltRecipient()
                .denomination("Galileo Bruno")
                .recipientType("PF")
                .taxId("CDCFSC11R99X001Z");
        var model = new NotificationAarRaddAlt()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recepient)
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel")
                .sendURL("TEST_sendURL")
                .sendURLLAbel("TEST_sendURLLAbel")
                .raddPhoneNumber("TEST_raddPhoneNumber")
                .senderLogoBase64(null);
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_analogDeliveryWorkflowFailureLegalFact() {
        var template = TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var recepient = new AnalogDeliveryWorkflowFailureRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        var model = new AnalogDeliveryWorkflowFailureLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowTime("TEST_endWorkflowTime")
                .recipient(recepient);
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }


    @Test
    void generate_analogDeliveryWorkflowTimeoutLegalFact() {
        var template = TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE };
        var recepient = new AnalogDeliveryWorkflowTimeoutRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z")
                .physicalAddress("TEST_PhysicalAddressAndDenomination");
        var model = new AnalogDeliveryWorkflowTimeoutLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowTime("TEST_endWorkflowTime")
                .attempt("0")
                .recipient(recepient);
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    /** HTML **/
    @Test
    void generate_notificationAarForEmail() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var sender = new AarForEmailSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarForEmailNotification()
                .iun("TEST_iun")
                .sender(sender);
        var model = new NotificationAarForEmail()
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .quickAccessLink("TEST_quickAccessLink")
                .pnFaqSendURL("TEST_pnFaqSendURL")
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .notification(notification);
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }


//    @Test
//    void generate_notificationCceForEmail() {
//        var template = TemplatesEnum.NOTIFICATION_CCE_FOR_EMAIL;
//        LanguageEnum[] langs = { LanguageEnum.IT };
//        var model = new NotificationCceForEmail()
//                .denomination("testDenomination")
//                .iun("testIun")
//                .ticketDate("testTicketDate")
//                .vrDate("testVrDate");
//        generateAndSaveDocument(template, langs, model, FileType.HTML);
//    }

    @Test
    void generate_notificationAarForPec() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_PEC;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var sender = new AarForPecSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarForPecNotification()
                .iun("TEST_iun")
                .subject("TEST_subject")
                .sender(sender);
        var recepient = new AarForPecRecipient()
                .taxId("CDCFSC11R99X001Z");
        var model = new NotificationAarForPec()
                .notification(notification)
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .quickAccessLink("TEST_quickAccessLink")
                .pnFaqSendURL("TEST_pnFaqSendURL")
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .recipientType("PF")
                .recipient(recepient);
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_mailVerificationCodeBody() {
        var template = TemplatesEnum.MAIL_VERIFICATION_CODE_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new MailVerificationCodeBody()
                .verificationCode("TEST_verificationCode")
                .recipientType("PG");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_pecVerificationCodeBody() {
        var template = TemplatesEnum.PEC_VERIFICATION_CODE_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new PecVerificationCodeBody()
                .verificationCode("TEST_verificationCode")
                .recipientType("PG");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_pecValidationContactsSuccessBody() {
        var template = TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new PecValidationContactsBody()
                .recipientType("PG");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_pecValidationContactsRejectBody() {
        var template = TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new PecValidationContactsBody()
                .recipientType("PG");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    /** TXT **/
    @Test
    void generate_notificationAarForSms() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_SMS;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var sender = new AarForSmsSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarForSmsNotification()
                .iun("TEST_iun")
                .sender(sender);
        var model = new NotificationAarForSms()
                .notification(notification);
        generateAndSaveDocument(template, langs, model, FileType.TXT);
    }

    @Test
    void generate_notificationAarForSubject() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_SUBJECT;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var sender = new AarForSubjectSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarForSubjectNotification()
                .sender(sender)
                .iun("TEST_iun");
        var model = new NotificationAarForSubject()
                .notification(notification);
        generateAndSaveDocument(template, langs, model, FileType.TXT);
    }

    @Test
    void generate_pecVerificationCodeSubject() {
        var template = TemplatesEnum.PEC_VERIFICATION_CODE_SUBJECT;
        LanguageEnum[] langs = { LanguageEnum.IT };
        generateAndSaveDocument(template, langs, null, FileType.TXT_NO_INPUT);
    }

    @Test
    void generate_smsVerificationCodeBody() {
        var template = TemplatesEnum.SMS_VERIFICATION_CODE_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        generateAndSaveDocument(template, langs, null, FileType.TXT_NO_INPUT);
    }

    @Test
    void generate_mailVerificationCodeSubject() {
        var template = TemplatesEnum.MAIL_VERIFICATION_CODE_SUBJECT;
        LanguageEnum[] langs = { LanguageEnum.IT };
        generateAndSaveDocument(template, langs, null, FileType.TXT_NO_INPUT);
    }

    @Test
    void generate_pecValidationContactsSuccessSubject() {
        var template = TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT;
        LanguageEnum[] langs = { LanguageEnum.IT };
        generateAndSaveDocument(template, langs, null, FileType.TXT_NO_INPUT);
    }

    @Test
    void generate_pecValidationContactsRejectSubject() {
        var template = TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_SUBJECT;
        LanguageEnum[] langs = { LanguageEnum.IT };
        generateAndSaveDocument(template, langs, null, FileType.TXT_NO_INPUT);
    }

    private void generateAndSaveDocument(TemplatesEnum template, LanguageEnum[] langs, Object model, FileType ext){
        for(var lang : langs) {
            Path filePath = Paths.get(TEST_DIR_NAME + File.separator + "test_" + template.getTemplate() +
                    "_" + lang.getValue() + ext.getExt());
            Assertions.assertDoesNotThrow(() -> {
                switch (ext){
                    case PDF -> {
                        var bytes = templateService.executePdfTemplate(template, lang, Mono.just(model)).block();
                        assert bytes != null;
                        Files.write(filePath, bytes);
                    }
                    case HTML, TXT -> {
                        var txt = templateService.executeTextTemplate(template, lang, Mono.just(model)).block();
                        assert txt != null;
                        Files.writeString(filePath, txt);
                    }
                    case HTML_NO_INPUT, TXT_NO_INPUT -> {
                        var txt = templateService.executeTextTemplate(template, lang).block();
                        assert txt != null;
                        Files.writeString(filePath, txt);
                    }
                }
            });
            System.out.print(template.getTemplate() + " successfully created at: " + filePath);
        }
    }

    @Getter
    enum FileType {
        PDF(".pdf"),
        HTML(".html"),
        HTML_NO_INPUT(".html"),
        TXT(".txt"),
        TXT_NO_INPUT(".txt");

        private final String ext;
        FileType(String ext) {
            this.ext = ext;
        }
    }
}
