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
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

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
        List<Tuple2<TemplatesEnum, Object>> templateModelList = buildTemplateModelList();

        for (LanguageEnum lang : langs) {
            List<byte[]> pdfDocuments = new ArrayList<>();

            for (Tuple2<TemplatesEnum, Object> entry : templateModelList) {
                Object model = entry.getT2();
                TemplatesEnum template = entry.getT1();

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

    private List<Tuple2<TemplatesEnum, Object>> buildTemplateModelList() {
        List<Tuple2<TemplatesEnum, Object>> list = new ArrayList<>();

        list.add(Tuples.of(TemplatesEnum.NOTIFICATION_AAR, buildNotificationAar()));
        list.add(Tuples.of(TemplatesEnum.NOTIFICATION_AAR_RADDALT, buildNotificationAarRaddAlt()));
        list.add(Tuples.of(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, buildNotificationReceivedLegalFact()));
        list.add(Tuples.of(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, buildPecDeliveryWorkflowLegalFactSuccess()));
        list.add(Tuples.of(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, buildPecDeliveryWorkflowLegalFactFailure()));
        list.add(Tuples.of(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT, buildNotificationViewedLegalFact()));
        list.add(Tuples.of(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT, buildNotificationCancelledLegalFact()));
        list.add(Tuples.of(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, buildAnalogDeliveryWorkflowFailureLegalFact()));

        return list;
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
                .denomination("Cleopatra Tea Filopatore")
                .digitalDomicile(new NotificationReceivedDigitalDomicile().address("test@dominioPec.it"))
                .taxId("FLPCPT69A65Z336P")
                .physicalAddressAndDenomination("Via di Test 123, Ipazia");
        var notification = new NotificationReceivedNotification()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .recipients(Collections.singletonList(recipient))
                .sender(new NotificationReceivedSender()
                        .paDenomination("Comune di Ipazia")
                        .paTaxId("0000000000"));
        return new NotificationReceivedLegalFact()
                .notification(notification)
                .digests(new ArrayList<>())
                .sendDate("01/01/1970")
                .subject("Titolo di test - FACSIMILE");
    }

    private Object buildPecDeliveryWorkflowLegalFactSuccess() {
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("Cleopatra Tea Filopatore")
                .taxId("FLPCPT69A65Z336P")
                .address("Via di Test 123, Ipazia")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("01/01/1970")
                .ok(true);
        return new PecDeliveryWorkflowLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .endWorkflowDate("01/01/1970")
                .endWorkflowStatus("TEST_endWorkflowStatus")
                .deliveries(Collections.singletonList(delivery));
    }
    private Object buildPecDeliveryWorkflowLegalFactFailure() {
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("Cleopatra Tea Filopatore")
                .taxId("FLPCPT69A65Z336P")
                .address("Via di Test 123, Ipazia")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("01/01/1970")
                .ok(false);
        return new PecDeliveryWorkflowLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .endWorkflowDate("01/01/1970")
                .endWorkflowStatus("TEST_endWorkflowStatus")
                .deliveries(Collections.singletonList(delivery));
    }

    private Object buildNotificationViewedLegalFact() {
        var recipient = new NotificationViewedRecipient()
                .denomination("Cleopatra Tea Filopatore")
                .taxId("FLPCPT69A65Z336P");
        var delegate = new NotificationViewedDelegate()
                .denomination("Galileo Bruno")
                .taxId("CDCFSC11R99X001Z");
        return new NotificationViewedLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .when("01/01/1970")
                .recipient(recipient)
                .delegate(delegate);
    }

    private Object buildNotificationCancelledLegalFact() {
        var recipient = new NotificationCancelledRecipient()
                .denomination("Cleopatra Tea Filopatore")
                .taxId("FLPCPT69A65Z336P");
        var sender = new NotificationCancelledSender()
                .paDenomination("Comune di Ipazia");
        var notification = new NotificationCancelledNotification()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .recipients(Collections.singletonList(recipient))
                .sender(sender);
        return new NotificationCancelledLegalFact()
                .notificationCancelledDate("01/01/1970")
                .notification(notification);
    }

    private Object buildNotificationAar() {
        var sender = new AarSender()
                .paDenomination("Comune di Ipazia");
        var notification = new AarNotification()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .sender(sender)
                .subject("Titolo di Esempio - FACSIMILE");
        var recipient = new AarRecipient()
                .recipientType("PF")
                .taxId("FLPCPT69A65Z336P");
        return new NotificationAar()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recipient)
                .piattaformaNotificheURL("cittadini.notifichedigitali.it")
                .piattaformaNotificheURLLabel("cittadini.notifichedigitali.it")
                .perfezionamentoURL("notifichedigitali.it/perfezionamento")
                .perfezionamentoURLLabel("notifichedigitali.it/perfezionamento");
    }

    private Object buildNotificationAarRaddAlt() {
        var sender = new AarRaddAltSender()
                .paDenomination("Comune di Ipazia");
        var notification = new AarRaddAltNotification()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .sender(sender)
                .subject("Titolo di Esempio - FACSIMILE");
        var recipient = new AarRaddAltRecipient()
                .denomination("Cleopatra Tea Filopatore")
                .recipientType("PF")
                .taxId("FLPCPT69A65Z336P");
        return new NotificationAarRaddAlt()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recipient)
                .piattaformaNotificheURL("cittadini.notifichedigitali.it")
                .piattaformaNotificheURLLabel("cittadini.notifichedigitali.it")
                .perfezionamentoURL("notifichedigitali.it/perfezionamento")
                .perfezionamentoURLLabel("notifichedigitali.it/perfezionamento")
                .sendURL("notifichedigitali.it")
                .sendURLLAbel("notifichedigitali.it")
                .raddPhoneNumber("06.9318.95.55")
                .senderLogoBase64(null);
    }

    private Object buildAnalogDeliveryWorkflowFailureLegalFact() {
        var recipient = new AnalogDeliveryWorkflowFailureRecipient()
                .denomination("Cleopatra Tea Filopatore")
                .taxId("FLPCPT69A65Z336P");
        return new AnalogDeliveryWorkflowFailureLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .endWorkflowDate("01/01/1970")
                .endWorkflowTime("00:00")
                .recipient(recipient);
    }

}
