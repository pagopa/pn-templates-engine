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
import java.util.List;

@SpringBootTest
public class DocumentGeneratorTest {
    private static final String TEST_DIR_NAME = "target" + File.separator + "generated-test-documents";
    private static final Path TEST_DIR_PATH = Paths.get(TEST_DIR_NAME);
    private static final String BASE64_QR = "data:image/png;base64,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";
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
        var model = new NotificationReceiverLegalFact()
                .notification(buildNotification())
                .digests(new ArrayList<>())
                .sendDate("TEST_sendDate")
                .subject("TEST_subject")
                .physicalAddressAndDenomination("TEST_physicalAddressAndDenomination");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_pecDeliveryWorkflowLegalFact() {
        var template = TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new PecDeliveryWorkflowLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowStatus("TEST_endWorkflowStatus")
                .deliveries(buildDeliveries());
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationViewedLegalFact() {
        var template = TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new NotificationViewedLegalFact()
                .iun("TEST_iun")
                .when("TEST_when")
                .recipient(buildRecipient())
                .delegate(buildDelegate());
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
                .timeReferenceEndDate("TEST_timeReferenceEndDate");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationCancelledLegalFact() {
        var template = TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new NotificationCancelledLegalFact()
                .notificationCancelledDate("TEST_startDate")
                .notification(buildNotification())
                .recipient(buildRecipient());
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationAAR() {
        var template = TemplatesEnum.NOTIFICATION_AAR;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new NotificationAAR()
                .notification(buildNotification())
                .qrCodeQuickAccessLink(BASE64_QR)
                .recipient(buildRecipient())
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel")
                .sendURL("TEST_sendURL")
                .sendURLLAbel("TEST_sendURLLAbel");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_notificationAAR_RADDalt() {
        var template = TemplatesEnum.NOTIFICATION_AAR_RADDALT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new NotificationAARRADDalt()
                .notification(buildNotification())
                .qrCodeQuickAccessLink(BASE64_QR)
                .recipient(buildRecipient())
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel")
                .sendURL("TEST_sendURL")
                .sendURLLAbel("TEST_sendURLLAbel")
                .raddPhoneNumber("TEST_");
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    @Test
    void generate_analogDeliveryWorkflowFailureLegalFact() {
        var template = TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new AnalogDeliveryWorkflowFailureLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowTime("TEST_endWorkflowTime")
                .recipient(buildRecipient());
        generateAndSaveDocument(template, langs, model, FileType.PDF);
    }

    /** HTML **/
    @Test
    void generate_notificationAARForEMAIL() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new NotificationAARForEMAIL()
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .quickAccessLink("TEST_quickAccessLink")
                .pnFaqSendURL("TEST_pnFaqSendURL")
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .notification(buildNotification());
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_notificationAARForPEC() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_PEC;
        LanguageEnum[] langs = { LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR };
        var model = new NotificationAARForPEC()
                .notification(buildNotification())
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .quickAccessLink("TEST_quickAccessLink")
                .pnFaqSendURL("TEST_pnFaqSendURL")
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .recipientType("PF")
                .recipient(buildRecipient());
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_mailVerificationCodeBody() {
        var template = TemplatesEnum.MAIL_VERIFICATION_CODE_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new MailVerificationCodeBody()
                .verificationCode("TEST_verificationCode");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_pecVerificationCodeBody() {
        var template = TemplatesEnum.PEC_VERIFICATION_CODE_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new PecVerificationCodeBody()
                .verificationCode("TEST_verificationCode");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_pecValidationContactsSuccessBody() {
        var template = TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new PecValidationContactsSuccessBody()
                .verificationCode("TEST_verificationCode");
        generateAndSaveDocument(template, langs, model, FileType.HTML);
    }

    @Test
    void generate_pecValidationContactsRejectBody() {
        var template = TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_BODY;
        LanguageEnum[] langs = { LanguageEnum.IT };
        generateAndSaveDocument(template, langs, null, FileType.HTML_NO_INPUT);
    }

    /** TXT **/
    @Test
    void generate_notificationAARForSMS() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_SMS;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new NotificationAARForSMS()
                .notification(buildNotification());
        generateAndSaveDocument(template, langs, model, FileType.TXT);
    }

    @Test
    void generate_notificationAARForSubject() {
        var template = TemplatesEnum.NOTIFICATION_AAR_FOR_SUBJECT;
        LanguageEnum[] langs = { LanguageEnum.IT };
        var model = new NotificationAARForSubject()
                .notification(buildNotification());
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

    private Notification buildNotification() {
        return new Notification()
                .iun("TEST_iun")
                .recipients(buildRecipients())
                .sender(buildSender())
                .subject("notification Titolo di 134 caratteri massimi spazi compresi. Aid olotielit, sed eiusmod tempora incidunt ue et et dolore magna aliqua aliqua aliqua");

    }
    private NotificationSender buildSender() {
        return new NotificationSender()
                .paDenomination("TEST_paDenomination")
                .paId("TEST_paId")
                .paTaxId("TEST_taxId");
    }

    private Recipient buildRecipient() {
        return new Recipient()
                .recipientType("PF")
                .denomination("Galileo Bruno")
                .digitalDomicile(new DigitalDomicile().address("test@dominioPec.it"))
                .taxId("CDCFSC11R99X001Z")
                .physicalAddress("TEST_physicalAddress");
    }
    private List<Recipient> buildRecipients() {
        return Collections.singletonList(buildRecipient());
    }

    private List<Delivery> buildDeliveries() {
        var delivery = new Delivery()
                .denomination("TEST_denomination")
                .taxId("TEST_taxId")
                .address("TEST_address")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("TEST_responseDate")
                .ok(true);
        return Collections.singletonList(delivery);
    }

    private Delegate buildDelegate() {
        return new Delegate()
                .denomination("TEST_denomination")
                .taxId("TEST_taxId");
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
