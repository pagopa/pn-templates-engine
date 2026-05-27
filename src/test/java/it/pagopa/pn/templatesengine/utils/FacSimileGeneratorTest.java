package it.pagopa.pn.templatesengine.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.service.TemplateService;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.templatesengine.config.TemplatesEnum.*;
import static it.pagopa.pn.templatesengine.utils.QrCodeUtils.getQrCodeQuickAccessUrlAarDetail;

/**
 * Represents a template entry with its enum, a unique key for the index, and the model object.
 */
record TemplateEntry(TemplatesEnum template, String key, Object model) {}

@SpringBootTest(properties = {"templatesStaticParams.facSimileMode=true"})
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

        // Build the list of PDF templates (without the index)
        List<TemplateEntry> templateEntries = buildTemplateEntries();

        for (LanguageEnum lang : langs) {
            List<byte[]> pdfDocuments = new ArrayList<>();
            List<String> generatedNames = new ArrayList<>();

            // 1. Generate all content PDFs
            for (TemplateEntry entry : templateEntries) {
                try {
                    byte[] pdfBytes = templateService.executePdfTemplate(entry.template(), lang, Mono.just(entry.model())).block();
                    if (pdfBytes != null && pdfBytes.length > 0) {
                        pdfDocuments.add(pdfBytes);
                        generatedNames.add(entry.key());
                        System.out.println("Generated " + entry.key() + " for language " + lang.getValue());
                    }
                } catch (Exception e) {
                    System.err.println("Skipping template " + entry.key() + " for language " + lang.getValue() + ": " + e.getMessage());
                }
            }

            if (!pdfDocuments.isEmpty()) {
                // 2. Generate a preliminary index to determine its page count
                IndexFacsimile preliminaryIndex = buildIndexModel(pdfDocuments, generatedNames, 1);
                byte[] preliminaryIndexPdf = templateService.executePdfTemplate(INDEX_FACSIMILE, lang, Mono.just(preliminaryIndex)).block();
                int indexPageCount = (preliminaryIndexPdf != null && preliminaryIndexPdf.length > 0)
                        ? countPages(preliminaryIndexPdf) : 1;

                // 3. Rebuild the index with correct page offset
                IndexFacsimile indexModel = buildIndexModel(pdfDocuments, generatedNames, indexPageCount);
                byte[] indexPdf = templateService.executePdfTemplate(INDEX_FACSIMILE, lang, Mono.just(indexModel)).block();

                // 4. Prepend index to all documents
                List<byte[]> allDocuments = new ArrayList<>();
                if (indexPdf != null && indexPdf.length > 0) {
                    allDocuments.add(indexPdf);
                }
                allDocuments.addAll(pdfDocuments);

                byte[] mergedPdf = mergePdfs(allDocuments);
                Path outputPath = TEST_DIR_PATH.resolve("ALL_" + lang.getValue() + ".pdf");
                Files.write(outputPath, mergedPdf);
                System.out.println("Merged " + allDocuments.size() + " PDFs (including index) for language " + lang.getValue() + " into: " + outputPath);
            }
        }
    }

    private IndexFacsimile buildIndexModel(List<byte[]> pdfDocuments, List<String> names, int indexPageCount) throws IOException {
        Map<String, String> pageNumberMap = new LinkedHashMap<>();
        int currentPage = indexPageCount + 1;

        for (int i = 0; i < pdfDocuments.size(); i++) {
            int pageCount = countPages(pdfDocuments.get(i));
            pageNumberMap.put(names.get(i), String.valueOf(currentPage));
            currentPage += pageCount;
        }

        return new IndexFacsimile()
                .notificationAarPageNumber(pageNumberMap.getOrDefault("notificationAar", "0"))
                .notificationAarRaddPageNumber(pageNumberMap.getOrDefault("notificationAarRaddAlt", "0"))
                .notificationReceivedLegalFactPageNumber(pageNumberMap.getOrDefault("notificationReceivedLegalFact", "0"))
                .pecDeliveryWorkflowLegalFactSuccessPageNumber(pageNumberMap.getOrDefault("pecDeliveryWorkflowLegalFact_success", "0"))
                .pecDeliveryWorkflowLegalFactFailurePageNumber(pageNumberMap.getOrDefault("pecDeliveryWorkflowLegalFact_failure", "0"))
                .notificationViewedLegalFactPageNumber(pageNumberMap.getOrDefault("notificationViewedLegalFact", "0"))
                .notificationCancelledLegalFactPageNumber(pageNumberMap.getOrDefault("notificationCancelledLegalFact", "0"))
                .analogDeliveryWorkflowFailureLegalFactPageNumber(pageNumberMap.getOrDefault("analogDeliveryWorkflowFailureLegalFact", "0"));
    }

    private int countPages(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(pdfBytes))) {
            return document.getNumberOfPages();
        }
    }

    private List<TemplateEntry> buildTemplateEntries() {
        return List.of(
                new TemplateEntry(NOTIFICATION_AAR, "notificationAar", buildNotificationAar()),
                new TemplateEntry(NOTIFICATION_AAR_RADDALT, "notificationAarRaddAlt", buildNotificationAarRaddAlt()),
                new TemplateEntry(NOTIFICATION_RECEIVED_LEGAL_FACT, "notificationReceivedLegalFact", buildNotificationReceivedLegalFact()),
                new TemplateEntry(PEC_DELIVERY_WORKFLOW_LEGAL_FACT, "pecDeliveryWorkflowLegalFact_success", buildPecDeliveryWorkflowLegalFactSuccess()),
                new TemplateEntry(PEC_DELIVERY_WORKFLOW_LEGAL_FACT, "pecDeliveryWorkflowLegalFact_failure", buildPecDeliveryWorkflowLegalFactFailure()),
                new TemplateEntry(NOTIFICATION_VIEWED_LEGAL_FACT, "notificationViewedLegalFact", buildNotificationViewedLegalFact()),
                new TemplateEntry(NOTIFICATION_CANCELLED_LEGAL_FACT, "notificationCancelledLegalFact", buildNotificationCancelledLegalFact()),
                new TemplateEntry(ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, "analogDeliveryWorkflowFailureLegalFact", buildAnalogDeliveryWorkflowFailureLegalFact())
        );
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
                .denomination("Nome Cognome")
                .digitalDomicile(new NotificationReceivedDigitalDomicile().address("test@dominioPec.it"))
                .taxId("AAAAAA00A00A000A")
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
                .sendDate("GG/MM/AAAA")
                .subject("Titolo di test - FACSIMILE");
    }

    private Object buildPecDeliveryWorkflowLegalFactSuccess() {
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("Nome Cognome")
                .taxId("AAAAAA00A00A000A")
                .address("Via di Test 123, Ipazia")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("GG/MM/AAAA")
                .ok(true);
        return new PecDeliveryWorkflowLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .endWorkflowDate("GG/MM/AAAA")
                .endWorkflowStatus("SUCCESS")
                .deliveries(Collections.singletonList(delivery));
    }
    private Object buildPecDeliveryWorkflowLegalFactFailure() {
        var delivery = new PecDeliveryWorkflowDelivery()
                .denomination("Nome Cognome")
                .taxId("AAAAAA00A00A000A")
                .address("Via di Test 123, Ipazia")
                .type("TEST_type")
                .addressSource("PLATFORM")
                .responseDate("GG/MM/AAAA")
                .ok(false);
        return new PecDeliveryWorkflowLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .endWorkflowDate("GG/MM/AAAA")
                .endWorkflowStatus("FAILURE")
                .deliveries(Collections.singletonList(delivery));
    }

    private Object buildNotificationViewedLegalFact() {
        var recipient = new NotificationViewedRecipient()
                .denomination("Nome Cognome")
                .taxId("AAAAAA00A00A000A");
        var delegate = new NotificationViewedDelegate()
                .denomination("Nome Cognome Delegato")
                .taxId("AAAAAA00A00A000A");
        return new NotificationViewedLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .when("GG/MM/AAAA")
                .recipient(recipient)
                .delegate(delegate);
    }

    private Object buildNotificationCancelledLegalFact() {
        var recipient = new NotificationCancelledRecipient()
                .denomination("Nome Cognome")
                .taxId("AAAAAA00A00A000A");
        var sender = new NotificationCancelledSender()
                .paDenomination("Comune di Ipazia");
        var notification = new NotificationCancelledNotification()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .recipients(Collections.singletonList(recipient))
                .sender(sender);
        return new NotificationCancelledLegalFact()
                .notificationCancelledDate("GG/MM/AAAA")
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
                .taxId("AAAAAA00A00A000A");
        return new NotificationAar()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recipient);
    }

    private Object buildNotificationAarRaddAlt() {
        var sender = new AarRaddAltSender()
                .paDenomination("Comune di Ipazia");
        var notification = new AarRaddAltNotification()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .sender(sender)
                .subject("Titolo di Esempio - FACSIMILE");
        var recipient = new AarRaddAltRecipient()
                .denomination("Nome Cognome")
                .recipientType("PF")
                .taxId("AAAAAA00A00A000A");
        return new NotificationAarRaddAlt()
                .notification(notification)
                .qrCodeQuickAccessLink(getQrCodeQuickAccessUrlAarDetail(CITTADINI_NOTIFICHEDIGITALI_IT_AAR_TEST))
                .recipient(recipient)
                .senderLogoBase64(null);
    }

    private Object buildAnalogDeliveryWorkflowFailureLegalFact() {
        var recipient = new AnalogDeliveryWorkflowFailureRecipient()
                .denomination("Nome Cognome")
                .taxId("AAAAAA00A00A000A");
        return new AnalogDeliveryWorkflowFailureLegalFact()
                .iun("AAAA-AAAA-AAAA-000000-A-0")
                .endWorkflowDate("GG/MM/AAAA")
                .endWorkflowTime("00:00")
                .recipient(recipient);
    }


}
