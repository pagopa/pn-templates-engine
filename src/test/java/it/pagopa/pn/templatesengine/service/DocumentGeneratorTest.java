package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
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
    private static final String TEST_DIR_NAME = "target" + File.separator + "generated-test-PDF";
    private static final Path TEST_DIR_PATH = Paths.get(TEST_DIR_NAME);
    @Autowired
    TemplateService templateService;

    @BeforeEach
    public void setup() throws IOException {
        // create target test folder, if not exists
        if (Files.notExists(TEST_DIR_PATH)) {
            Files.createDirectory(TEST_DIR_PATH);
        }
    }
    //@Test
    void generateNotificationReceivedLegalFactTest() {
        var template = TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT;
        var lang = LanguageEnum.IT;
        var model = new NotificationReceiverLegalFact()
                .notification(buildNotification())
                .digests(new ArrayList<>())
                .sendDate("TODO")
                .subject("TODO")
                .physicalAddressAndDenomination("TODO");
        Path filePath = Paths.get(TEST_DIR_NAME + File.separator + "test_ReceivedLegalFact.pdf");
        Assertions.assertDoesNotThrow(() -> {
            var bytes = templateService.executePdfTemplate(template, lang, Mono.just(model)).block();
            assert bytes != null;
            Files.write(filePath, bytes);
        });
        System.out.print("*** ReceivedLegalFact pdf successfully created at: " + filePath);
    }

    private Notification buildNotification() {
        return new Notification()
                .iun("Example_IUN_1234_Test")
                .recipients(buildRecipients())
                .sender(createSender())
                .subject("notification Titolo di 134 caratteri massimi spazi compresi. Aid olotielit, sed eiusmod tempora incidunt ue et et dolore magna aliqua aliqua aliqua");

    }
    private NotificationSender createSender() {
        return new NotificationSender()
                .paDenomination("TEST_PA_DENOMINATION")
                .paId("TEST_PA_ID")
                .paTaxId("TEST_TAX_ID");
    }
    private List<Recipient> buildRecipients() {
        var recipient = new Recipient()
                .recipientType("PF")
                .denomination("Galileo Bruno")
                .digitalDomicile(new DigitalDomicile().address("test@dominioPec.it"))
                .taxId("CDCFSC11R99X001Z")
                .physicalAddress("");
        return Collections.singletonList(recipient);
    }
}
