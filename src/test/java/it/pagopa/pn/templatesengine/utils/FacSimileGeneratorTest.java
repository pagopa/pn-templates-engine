package it.pagopa.pn.templatesengine.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.service.TemplateService;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.templatesengine.utils.QrCodeUtils.getQrCodeQuickAccessUrlAarDetail;

@SpringBootTest
public class FacSimileGeneratorTest {
    private static final String TEST_DIR_NAME = "target" + File.separator + "generated-test-documents" + File.separator + "fac-simile";
    private static final Path TEST_DIR_PATH = Paths.get(TEST_DIR_NAME);
    public static final String CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST = "https://cittadini.notifichedigitali.it/?aar=V1JXSi1QTUpZLUhMUlUtMjAyNTAxLU4tMV9QRi01MDI0Y2Q1Yi00NTE2LTQ5OWMtOTdiNi0wMDY5YmM4ODI1ODlfZGE4NjVmOTItNDljZS00NzYxLTkwM2EtYTg3MDEyZWM1N2Nm";

    @Autowired
    TemplateService templateService;

    @BeforeEach
    public void setup() throws IOException {
        if (Files.notExists(TEST_DIR_PATH)) {
            Files.createDirectories(TEST_DIR_PATH);
        }
    }

    @Test
    void generateFacSimilePerLanguage() throws IOException {
        LanguageEnum[] langs = {LanguageEnum.IT, LanguageEnum.DE, LanguageEnum.SL, LanguageEnum.FR};

        // Build the map of all PDF templates with their test models
        Map<TemplatesEnum, Object> templateModelMap = buildTemplateModelMap();

        for (LanguageEnum lang : langs) {
            List<byte[]> pdfDocuments = new ArrayList<>();

            for (Map.Entry<TemplatesEnum, Object> entry : templateModelMap.entrySet()) {
                TemplatesEnum template = entry.getKey();
                Object model = entry.getValue();

                try {
                    byte[] pdfBytes = templateService.executePdfTemplate(template, lang, Mono.just(model)).block();
                    if (pdfBytes != null && pdfBytes.length > 0) {
                        pdfDocuments.add(pdfBytes);
                        System.out.println("Generated " + template.getTemplate() + " for language " + lang.getValue());
                    }
                } catch (Exception e) {
                    System.err.println("Skipping template " + template.getTemplate() + " for language " + lang.getValue() + ": " + e.getMessage());
                }
            }

            if (!pdfDocuments.isEmpty()) {
                byte[] mergedPdf = mergePdfs(pdfDocuments);
                Path outputPath = TEST_DIR_PATH.resolve("ALL_" + lang.getValue() + ".pdf");
                Files.write(outputPath, mergedPdf);
                System.out.println("Merged " + pdfDocuments.size() + " PDFs for language " + lang.getValue() + " into: " + outputPath);
            }
        }
    }

    private Map<TemplatesEnum, Object> buildTemplateModelMap() {
        Map<TemplatesEnum, Object> map = new LinkedHashMap<>();

        map.put(TemplatesEnum.NOTIFICATION_AAR, buildNotificationAar());
        map.put(TemplatesEnum.NOTIFICATION_AAR_RADDALT, buildNotificationAarRaddAlt());
        map.put(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, buildNotificationReceivedLegalFact());
        map.put(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, buildPecDeliveryWorkflowLegalFactSuccess());
        map.put(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT, buildNotificationViewedLegalFact());
        map.put(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT, buildNotificationCancelledLegalFact());
        map.put(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, buildAnalogDeliveryWorkflowFailureLegalFact());

        return map;
    }

    private byte[] mergePdfs(List<byte[]> pdfDocuments) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        merger.setDestinationStream(outputStream);

        for (byte[] pdf : pdfDocuments) {
            merger.addSource(new RandomAccessReadBuffer(pdf));
        }

        merger.mergeDocuments(null);
        return outputStream.toByteArray();
    }

    // --- Model builders ---

    private Object buildNotificationReceivedLegalFact() {
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
        return new NotificationReceivedLegalFact()
                .notification(notification)
                .digests(new ArrayList<>())
                .sendDate("TEST_sendDate")
                .subject("TEST_subject");
    }

    private Object buildPecDeliveryWorkflowLegalFactSuccess() {
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("TEST_denomination")
                .taxId("TEST_taxId")
                .address("TEST_address")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("TEST_responseDate")
                .ok(true);
        return new PecDeliveryWorkflowLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowStatus("TEST_endWorkflowStatus")
                .deliveries(Collections.singletonList(delivery));
    }
    private Object buildPecDeliveryWorkflowLegalFactFailure() {
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("TEST_denomination")
                .taxId("TEST_taxId")
                .address("TEST_address")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("TEST_responseDate")
                .ok(false);
        return new PecDeliveryWorkflowLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowStatus("TEST_endWorkflowStatus")
                .deliveries(Collections.singletonList(delivery));
    }

    private Object buildNotificationViewedLegalFact() {
        var recipient = new NotificationViewedRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        var delegate = new NotificationViewedDelegate()
                .denomination("Mario Rossi")
                .taxId("MRRSSC11R99X001Z");
        return new NotificationViewedLegalFact()
                .iun("TEST_iun")
                .when("TEST_when")
                .recipient(recipient)
                .delegate(delegate);
    }

    private Object buildMalfunctionLegalFact() {
        return new MalfunctionLegalFact()
                .startDate("TEST_startDate")
                .timeReferenceStartDate("TEST_timeReferenceStartDate")
                .endDate("TEST_endDate")
                .timeReferenceEndDate("TEST_timeReferenceEndDate")
                .htmlDescription("<p>Sample <b>TEST_description</b></p>");
    }

    private Object buildNotificationCancelledLegalFact() {
        var recipient = new NotificationCancelledRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        var sender = new NotificationCancelledSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new NotificationCancelledNotification()
                .iun("TEST")
                .recipients(Collections.singletonList(recipient))
                .sender(sender);
        return new NotificationCancelledLegalFact()
                .notificationCancelledDate("TEST_startDate")
                .notification(notification);
    }

    private Object buildNotificationAar() {
        var sender = new AarSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarNotification()
                .iun("TEST_iun")
                .sender(sender)
                .subject("notification Titolo di 134 caratteri massimi spazi compresi. Aid olotielit, sed eiusmod tempora incidunt ue et et dolore magna aliqua aliqua aliqua");
        var recipient = new AarRecipient()
                .recipientType("PF")
                .taxId("CDCFSC11R99X001Z");
        return new NotificationAar()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recipient)
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel");
    }

    private Object buildNotificationAarRaddAlt() {
        var sender = new AarRaddAltSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarRaddAltNotification()
                .iun("TEST_iun")
                .sender(sender)
                .subject("notification Titolo di 134 caratteri massimi spazi compresi. Aid olotielit, sed eiusmod tempora incidunt ue et et dolore magna aliqua aliqua aliqua");
        var recipient = new AarRaddAltRecipient()
                .denomination("Galileo Bruno")
                .recipientType("PF")
                .taxId("CDCFSC11R99X001Z");
        return new NotificationAarRaddAlt()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recipient)
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel")
                .sendURL("TEST_sendURL")
                .sendURLLAbel("TEST_sendURLLAbel")
                .raddPhoneNumber("TEST_raddPhoneNumber")
                .senderLogoBase64(null);
    }

    private Object buildAnalogDeliveryWorkflowFailureLegalFact() {
        var recipient = new AnalogDeliveryWorkflowFailureRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        return new AnalogDeliveryWorkflowFailureLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowTime("TEST_endWorkflowTime")
                .recipient(recipient);
    }

    private Object buildAnalogDeliveryWorkflowTimeoutLegalFact() {
        var recipient = new AnalogDeliveryWorkflowTimeoutRecipient()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z")
                .physicalAddress("TEST_PhysicalAddressAndDenomination");
        return new AnalogDeliveryWorkflowTimeoutLegalFact()
                .iun("TEST_iun")
                .endWorkflowDate("TEST_endWorkflowDate")
                .endWorkflowTime("TEST_endWorkflowTime")
                .attempt("0")
                .recipient(recipient);
    }

    private Object buildAnalogFeedbackAvailabilityStatement() {
        return new AnalogFeedbackAvailabilityStatement()
                .iun("TEST_iun")
                .declarationDate("TEST_declarationDate")
                .senderDenomination("TEST_senderDenomination")
                .registeredLetterCode("TEST_registeredLetterCode")
                .senderTaxId("TEST_senderTaxId");
    }

}
