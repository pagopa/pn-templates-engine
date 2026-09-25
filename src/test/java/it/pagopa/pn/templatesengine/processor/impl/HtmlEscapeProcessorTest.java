package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class HtmlEscapeProcessorTest {

    private static final String RAW_SUBJECT = "<<pratica 1234>>";
    private static final String ESCAPED_SUBJECT = "&lt;&lt;pratica 1234&gt;&gt;";

    private final HtmlEscapeProcessor processor = new HtmlEscapeProcessor();

    private void run(TemplatesEnum template, Object model) {
        StepVerifier.create(processor.process(template, model, null)).verifyComplete();
    }

    @Test
    void shouldEscapeNotificationAar() {
        AarSender sender = new AarSender();
        sender.setPaDenomination("Comune di \"Test\" & C.");
        AarNotification notification = new AarNotification();
        notification.setIun("AAAA-BBBB-CCC-202607-W-1");
        notification.setSubject(RAW_SUBJECT);
        notification.setSender(sender);
        NotificationAar model = new NotificationAar();
        model.setNotification(notification);
        model.setQrCodeQuickAccessLink("https://x.it?a=1&b=2");

        run(TemplatesEnum.NOTIFICATION_AAR, model);

        Assertions.assertEquals(ESCAPED_SUBJECT, model.getNotification().getSubject());
        Assertions.assertEquals("Comune di &quot;Test&quot; &amp; C.", model.getNotification().getSender().getPaDenomination());
        Assertions.assertEquals("AAAA-BBBB-CCC-202607-W-1", model.getNotification().getIun());
        Assertions.assertEquals("https://x.it?a=1&b=2", model.getQrCodeQuickAccessLink());
    }

    @Test
    void shouldEscapeNotificationAarRaddAlt() {
        AarRaddAltSender sender = new AarRaddAltSender();
        sender.setPaDenomination("Comune <X>");
        AarRaddAltNotification notification = new AarRaddAltNotification();
        notification.setSubject(RAW_SUBJECT);
        notification.setSender(sender);
        AarRaddAltRecipient recipient = new AarRaddAltRecipient();
        recipient.setDenomination("Mario <Rossi>");
        NotificationAarRaddAlt model = new NotificationAarRaddAlt();
        model.setNotification(notification);
        model.setRecipient(recipient);

        run(TemplatesEnum.NOTIFICATION_AAR_RADDALT, model);

        Assertions.assertEquals(ESCAPED_SUBJECT, model.getNotification().getSubject());
        Assertions.assertEquals("Comune &lt;X&gt;", model.getNotification().getSender().getPaDenomination());
        Assertions.assertEquals("Mario &lt;Rossi&gt;", model.getRecipient().getDenomination());
    }

    @Test
    void shouldEscapeNotificationAarForPecAndEmail() {
        AarForPecSender pecSender = new AarForPecSender();
        pecSender.setPaDenomination("Comune & Co");
        AarForPecNotification pecNotification = new AarForPecNotification();
        pecNotification.setSubject(RAW_SUBJECT);
        pecNotification.setSender(pecSender);
        NotificationAarForPec pecModel = new NotificationAarForPec();
        pecModel.setNotification(pecNotification);

        run(TemplatesEnum.NOTIFICATION_AAR_FOR_PEC, pecModel);

        Assertions.assertEquals(ESCAPED_SUBJECT, pecModel.getNotification().getSubject());
        Assertions.assertEquals("Comune &amp; Co", pecModel.getNotification().getSender().getPaDenomination());

        AarForEmailSenderDigital emailSender = new AarForEmailSenderDigital();
        emailSender.setPaDenomination("Comune <Y>");
        AarForEmailNotificationDigital emailNotification = new AarForEmailNotificationDigital();
        emailNotification.setSender(emailSender);
        NotificationAarForEmailDigital emailModel = new NotificationAarForEmailDigital();
        emailModel.setNotification(emailNotification);

        run(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL_DIGITAL, emailModel);

        Assertions.assertEquals("Comune &lt;Y&gt;", emailModel.getNotification().getSender().getPaDenomination());
    }

    @Test
    void shouldEscapeNotificationReceivedLegalFactIncludingRecipients() {
        NotificationReceivedSender sender = new NotificationReceivedSender();
        sender.setPaDenomination("Comune <Z>");
        NotificationReceivedRecipient first = new NotificationReceivedRecipient();
        first.setDenomination("Mario <Rossi>");
        first.setPhysicalAddressAndDenomination("Via Roma 1 <interno 2>");
        NotificationReceivedRecipient second = new NotificationReceivedRecipient();
        second.setDenomination("Anna & Co");
        NotificationReceivedNotification notification = new NotificationReceivedNotification();
        notification.setSender(sender);
        notification.setRecipients(List.of(first, second));
        NotificationReceivedLegalFact model = new NotificationReceivedLegalFact();
        model.setSubject(RAW_SUBJECT);
        model.setNotification(notification);

        run(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, model);

        Assertions.assertEquals(ESCAPED_SUBJECT, model.getSubject());
        Assertions.assertEquals("Comune &lt;Z&gt;", model.getNotification().getSender().getPaDenomination());
        Assertions.assertEquals("Mario &lt;Rossi&gt;", model.getNotification().getRecipients().get(0).getDenomination());
        Assertions.assertEquals("Via Roma 1 &lt;interno 2&gt;", model.getNotification().getRecipients().get(0).getPhysicalAddressAndDenomination());
        Assertions.assertEquals("Anna &amp; Co", model.getNotification().getRecipients().get(1).getDenomination());
    }

    @Test
    void shouldEscapeInformalCommunicationWithoutTouchingBody() {
        InformalCommunicationSender sender = new InformalCommunicationSender();
        sender.setDenomination("Comune <W>");
        sender.setService("Servizio & Tributi");
        SharedInformalCommunicationRecipient recipient = new SharedInformalCommunicationRecipient();
        recipient.setDenomination("Mario <Rossi>");
        InformalCommunicationBody body = new InformalCommunicationBody();
        body.setPrimaryContent("**testo** con <b>markup</b>");
        InformalCommunication model = new InformalCommunication();
        model.setSubject(RAW_SUBJECT);
        model.setSender(sender);
        model.setRecipient(recipient);
        model.setBody(body);

        run(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, model);

        Assertions.assertEquals(ESCAPED_SUBJECT, model.getSubject());
        Assertions.assertEquals("Comune &lt;W&gt;", model.getSender().getDenomination());
        Assertions.assertEquals("Servizio &amp; Tributi", model.getSender().getService());
        Assertions.assertEquals("Mario &lt;Rossi&gt;", model.getRecipient().getDenomination());
        Assertions.assertEquals("**testo** con <b>markup</b>", model.getBody().getPrimaryContent());
    }

    @Test
    void shouldEscapeLinkAndImageMarkupInSubjectAndDenominations() {
        AarSender sender = new AarSender();
        sender.setPaDenomination("<img src=x onerror=alert(1)>Comune");
        AarNotification notification = new AarNotification();
        notification.setSubject("Avviso <a href=\"https://evil.it\">clicca qui</a> e ![logo](https://evil.it/l.png)");
        notification.setSender(sender);
        NotificationAar model = new NotificationAar();
        model.setNotification(notification);

        run(TemplatesEnum.NOTIFICATION_AAR, model);

        Assertions.assertEquals(
                "Avviso &lt;a href=&quot;https://evil.it&quot;&gt;clicca qui&lt;/a&gt; e ![logo](https://evil.it/l.png)",
                model.getNotification().getSubject());
        Assertions.assertEquals("&lt;img src=x onerror=alert(1)&gt;Comune",
                model.getNotification().getSender().getPaDenomination());
    }

    @Test
    void shouldEscapeJavascriptProtocolInTextFields() {
        AarForPecSender sender = new AarForPecSender();
        sender.setPaDenomination("<a href='javascript:alert(1)'>Comune</a>");
        AarForPecNotification notification = new AarForPecNotification();
        notification.setSubject("\"><script>location='javascript:alert(1)'</script>");
        notification.setSender(sender);
        NotificationAarForPec model = new NotificationAarForPec();
        model.setNotification(notification);

        run(TemplatesEnum.NOTIFICATION_AAR_FOR_PEC, model);

        Assertions.assertEquals(
                "&quot;&gt;&lt;script&gt;location=&#39;javascript:alert(1)&#39;&lt;/script&gt;",
                model.getNotification().getSubject());
        Assertions.assertEquals(
                "&lt;a href=&#39;javascript:alert(1)&#39;&gt;Comune&lt;/a&gt;",
                model.getNotification().getSender().getPaDenomination());
    }

    @Test
    void shouldEscapeDigitalDomicileAddressOfRecipients() {
        NotificationReceivedDigitalDomicile domicile = new NotificationReceivedDigitalDomicile();
        domicile.setAddress("Ufficio Tributi <tributi@pec.comune.it>");
        NotificationReceivedRecipient recipient = new NotificationReceivedRecipient();
        recipient.setDigitalDomicile(domicile);
        NotificationReceivedNotification notification = new NotificationReceivedNotification();
        notification.setRecipients(List.of(recipient));
        NotificationReceivedLegalFact model = new NotificationReceivedLegalFact();
        model.setNotification(notification);

        run(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, model);

        Assertions.assertEquals("Ufficio Tributi &lt;tributi@pec.comune.it&gt;",
                model.getNotification().getRecipients().get(0).getDigitalDomicile().getAddress());
    }

    @Test
    void shouldEscapeNotificationCancelledLegalFact() {
        NotificationCancelledSender sender = new NotificationCancelledSender();
        sender.setPaDenomination("Comune <X>");
        NotificationCancelledRecipient recipient = new NotificationCancelledRecipient();
        recipient.setDenomination("Mario <Rossi>");
        NotificationCancelledNotification notification = new NotificationCancelledNotification();
        notification.setSender(sender);
        notification.setRecipients(List.of(recipient));
        NotificationCancelledLegalFact model = new NotificationCancelledLegalFact();
        model.setNotification(notification);

        run(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT, model);

        Assertions.assertEquals("Comune &lt;X&gt;", model.getNotification().getSender().getPaDenomination());
        Assertions.assertEquals("Mario &lt;Rossi&gt;", model.getNotification().getRecipients().get(0).getDenomination());
    }

    @Test
    void shouldEscapePecDeliveryWorkflowLegalFact() {
        PecDeliveryWorkflowDelivery delivery = new PecDeliveryWorkflowDelivery();
        delivery.setDenomination("Comune <X>");
        delivery.setAddress("Ufficio <pec@comune.it>");
        PecDeliveryWorkflowLegalFact model = new PecDeliveryWorkflowLegalFact();
        model.setDeliveries(List.of(delivery));

        run(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, model);

        Assertions.assertEquals("Comune &lt;X&gt;", model.getDeliveries().get(0).getDenomination());
        Assertions.assertEquals("Ufficio &lt;pec@comune.it&gt;", model.getDeliveries().get(0).getAddress());
    }

    @Test
    void shouldEscapeAnalogWorkflowAndViewedLegalFacts() {
        AnalogDeliveryWorkflowFailureRecipient failureRecipient = new AnalogDeliveryWorkflowFailureRecipient();
        failureRecipient.setDenomination("Mario <Rossi>");
        AnalogDeliveryWorkflowFailureLegalFact failure = new AnalogDeliveryWorkflowFailureLegalFact();
        failure.setRecipient(failureRecipient);
        run(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, failure);
        Assertions.assertEquals("Mario &lt;Rossi&gt;", failure.getRecipient().getDenomination());

        AnalogDeliveryWorkflowTimeoutRecipient timeoutRecipient = new AnalogDeliveryWorkflowTimeoutRecipient();
        timeoutRecipient.setDenomination("Anna & Co");
        timeoutRecipient.setPhysicalAddress("Via Roma 1 <interno 2>");
        AnalogDeliveryWorkflowTimeoutLegalFact timeout = new AnalogDeliveryWorkflowTimeoutLegalFact();
        timeout.setRecipient(timeoutRecipient);
        run(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT, timeout);
        Assertions.assertEquals("Anna &amp; Co", timeout.getRecipient().getDenomination());
        Assertions.assertEquals("Via Roma 1 &lt;interno 2&gt;", timeout.getRecipient().getPhysicalAddress());

        NotificationViewedRecipient viewedRecipient = new NotificationViewedRecipient();
        viewedRecipient.setDenomination("Mario <Rossi>");
        NotificationViewedDelegate delegate = new NotificationViewedDelegate();
        delegate.setDenomination("Luigi <Bianchi>");
        NotificationViewedLegalFact viewed = new NotificationViewedLegalFact();
        viewed.setRecipient(viewedRecipient);
        viewed.setDelegate(delegate);
        run(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT, viewed);
        Assertions.assertEquals("Mario &lt;Rossi&gt;", viewed.getRecipient().getDenomination());
        Assertions.assertEquals("Luigi &lt;Bianchi&gt;", viewed.getDelegate().getDenomination());
    }

    @Test
    void shouldEscapeAnalogFeedbackStatementAndCceForEmail() {
        AnalogFeedbackAvailabilityStatement statement = new AnalogFeedbackAvailabilityStatement();
        statement.setSenderDenomination("Comune <X>");
        run(TemplatesEnum.ANALOG_FEEDBACK_AVAILABILITY_STATEMENT, statement);
        Assertions.assertEquals("Comune &lt;X&gt;", statement.getSenderDenomination());

        NotificationCceForEmail cce = new NotificationCceForEmail();
        cce.setDenomination("Mario <Rossi>");
        run(TemplatesEnum.NOTIFICATION_CCE_FOR_EMAIL, cce);
        Assertions.assertEquals("Mario &lt;Rossi&gt;", cce.getDenomination());
    }

    @Test
    void shouldIgnoreNullModelNullBranchesAndUnknownTypes() {
        NotificationAar emptyModel = new NotificationAar();

        Assertions.assertDoesNotThrow(() -> {
            run(TemplatesEnum.NOTIFICATION_AAR, emptyModel);
            run(TemplatesEnum.NOTIFICATION_AAR, null);
            run(TemplatesEnum.NOTIFICATION_AAR_FOR_SUBJECT, new NotificationAarForSubject());
        });
        Assertions.assertNull(emptyModel.getNotification());
    }

    @Test
    void shouldUseFreemarkerHtmlEscaping() {
        Assertions.assertEquals("&amp;&lt;&gt;&quot;&#39;", HtmlEscapeProcessor.escapeHtml("&<>\"'"));
        Assertions.assertNull(HtmlEscapeProcessor.escapeHtml(null));
        Assertions.assertEquals("", HtmlEscapeProcessor.escapeHtml(""));
        Assertions.assertEquals("nessun carattere speciale", HtmlEscapeProcessor.escapeHtml("nessun carattere speciale"));
    }
}
