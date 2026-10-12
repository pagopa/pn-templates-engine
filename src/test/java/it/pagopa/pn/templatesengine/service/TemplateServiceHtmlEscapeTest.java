package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
class TemplateServiceHtmlEscapeTest {

    @Autowired
    TemplateService templateService;

    @Test
    void shouldEscapeSubjectOnAarTemplate() {
        NotificationAar model = new NotificationAar();
        model.setQrCodeQuickAccessLink("https://cittadini.notifichedigitali.it/link");
        AarNotification notification = new AarNotification();
        notification.setIun("AAAA-BBBB-CCCC-202607-W-1");
        notification.setSubject("<<pratica 12345>>");
        AarSender sender = new AarSender();
        sender.setPaDenomination("Comune di Test");
        notification.setSender(sender);
        model.setNotification(notification);
        AarRecipient recipient = new AarRecipient();
        recipient.setRecipientType("PF");
        recipient.setTaxId("CLMCST42R12D969Z");
        model.setRecipient(recipient);

        String html = templateService.executeTextTemplate(TemplatesEnum.NOTIFICATION_AAR, LanguageEnum.IT, Mono.just(model)).block();

        Assertions.assertNotNull(html);
        Assertions.assertTrue(html.contains("&lt;&lt;pratica 12345&gt;&gt;"),
                "Il subject deve essere presente in forma escapata");
        Assertions.assertFalse(html.contains("<<pratica"), "Il subject non deve essere interpretato come markup");
    }
}
