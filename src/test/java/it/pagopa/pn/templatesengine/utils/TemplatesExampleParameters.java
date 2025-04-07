package it.pagopa.pn.templatesengine.utils;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;

public class TemplatesExampleParameters {
    private TemplatesExampleParameters(){}

    public static NotificationAar getNotificationAar() {
        var sender = new AarSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarNotification()
                .iun("TEST_iun")
                .sender(sender)
                .subject("TEST");
        var recipient = new AarRecipient()
                .recipientType("TEST_PF")
                .taxId("CDCFSC11R99X001Z");
        return new NotificationAar()
                .notification(notification)
                .qrCodeQuickAccessLink("TEST_QR")
                .recipient(recipient)
                .piattaformaNotificheURL("TEST_piattaformaNotificheURL")
                .piattaformaNotificheURLLabel("TEST_piattaformaNotificheURLLabel")
                .perfezionamentoURL("TEST_perfezionamentoURL")
                .perfezionamentoURLLabel("TEST_perfezionamentoURLLabel")
                .senderLogoBase64(null);
    }

    public static NotificationAarRaddAlt getNotificationAarRaddAlt() {
        var sender = new AarRaddAltSender()
                .paDenomination("TEST_PaDenomination");
        var notification = new AarRaddAltNotification()
                .iun("TEST_iun")
                .sender(sender)
                .subject("TEST");
        var recipient = new AarRaddAltRecipient()
                .denomination("Galileo Bruno")
                .recipientType("PF")
                .taxId("CDCFSC11R99X001Z");
        return new NotificationAarRaddAlt()
                .notification(notification)
                .qrCodeQuickAccessLink("TEST_QR")
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
}
