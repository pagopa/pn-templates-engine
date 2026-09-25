package it.pagopa.pn.templatesengine.processor.impl;

import freemarker.core.HTMLOutputFormat;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Processore che applica l'HTML escaping ai campi testuali valorizzati liberamente dall'ente,
 * convertendo in entity HTML i caratteri che il renderer interpreterebbe come markup
 * (es. {@code <<pratica 123>>} diventa {@code &lt;&lt;pratica 123&gt;&gt;}).
 *
 * <p>L'escaping è <b>opt-in per model</b>: i campi da bonificare sono dichiarati qui,
 * in modo esplicito e type-safe, per ciascun tipo di richiesta. I model non gestiti e i campi
 * non dichiarati restano invariati, così i valori che devono essere interpretati come HTML
 * (es. il body Markdown convertito da {@link MarkdownToHtmlProcessor}) non vengono alterati.</p>
 *
 * <p>Va registrato come <b>primo</b> step della chain, per operare sul valore originale
 * e non sull'output prodotto dagli altri processor.</p>
 */
@Slf4j
@Component
public class HtmlEscapeProcessor implements TemplateModelProcessor<Object, Object> {

    /**
     * Applica l'escaping in-place sui campi previsti per il tipo di model ricevuto.
     *
     * @param template  il template in fase di elaborazione
     * @param model     il model di request da bonificare
     * @param outParams non utilizzato: il processore opera direttamente sul model
     * @return un Mono che completa immediatamente
     */
    @Override
    public Mono<Void> process(TemplatesEnum template, Object model, Object outParams) {
        switch (model) {
            case NotificationAar m -> {
                escape(m.getNotification(), AarNotification::getSubject, AarNotification::setSubject);
                escape(nav(m.getNotification(), AarNotification::getSender),
                        AarSender::getPaDenomination, AarSender::setPaDenomination);
            }
            case NotificationAarRaddAlt m -> {
                escape(m.getNotification(), AarRaddAltNotification::getSubject, AarRaddAltNotification::setSubject);
                escape(nav(m.getNotification(), AarRaddAltNotification::getSender),
                        AarRaddAltSender::getPaDenomination, AarRaddAltSender::setPaDenomination);
                escape(m.getRecipient(), AarRaddAltRecipient::getDenomination, AarRaddAltRecipient::setDenomination);
            }
            case NotificationAarForPec m -> {
                escape(m.getNotification(), AarForPecNotification::getSubject, AarForPecNotification::setSubject);
                escape(nav(m.getNotification(), AarForPecNotification::getSender),
                        AarForPecSender::getPaDenomination, AarForPecSender::setPaDenomination);
            }
            case NotificationAarForEmailAnalog m ->
                    escape(nav(m.getNotification(), AarForEmailNotificationAnalog::getSender),
                            AarForEmailSenderAnalog::getPaDenomination, AarForEmailSenderAnalog::setPaDenomination);
            case NotificationAarForEmailDigital m ->
                    escape(nav(m.getNotification(), AarForEmailNotificationDigital::getSender),
                            AarForEmailSenderDigital::getPaDenomination, AarForEmailSenderDigital::setPaDenomination);
            case NotificationReceivedLegalFact m -> {
                escape(m, NotificationReceivedLegalFact::getSubject, NotificationReceivedLegalFact::setSubject);
                escape(nav(m.getNotification(), NotificationReceivedNotification::getSender), NotificationReceivedSender::getPaDenomination, NotificationReceivedSender::setPaDenomination);
                List<NotificationReceivedRecipient> recipients = nav(m.getNotification(), NotificationReceivedNotification::getRecipients);
                if (recipients != null) {
                    recipients.forEach(recipient -> {
                        escape(recipient, NotificationReceivedRecipient::getDenomination, NotificationReceivedRecipient::setDenomination);
                        escape(recipient, NotificationReceivedRecipient::getPhysicalAddressAndDenomination, NotificationReceivedRecipient::setPhysicalAddressAndDenomination);
                        escape(nav(recipient, NotificationReceivedRecipient::getDigitalDomicile),
                                NotificationReceivedDigitalDomicile::getAddress, NotificationReceivedDigitalDomicile::setAddress);
                    });
                }
            }
            case NotificationCancelledLegalFact m -> {
                escape(nav(m.getNotification(), NotificationCancelledNotification::getSender),
                        NotificationCancelledSender::getPaDenomination, NotificationCancelledSender::setPaDenomination);
                List<NotificationCancelledRecipient> recipients = nav(m.getNotification(), NotificationCancelledNotification::getRecipients);
                if (recipients != null) {
                    recipients.forEach(recipient -> escape(recipient,
                            NotificationCancelledRecipient::getDenomination, NotificationCancelledRecipient::setDenomination));
                }
            }
            case PecDeliveryWorkflowLegalFact m -> {
                if (m.getDeliveries() != null) {
                    m.getDeliveries().forEach(delivery -> {
                        escape(delivery, PecDeliveryWorkflowDelivery::getDenomination, PecDeliveryWorkflowDelivery::setDenomination);
                        escape(delivery, PecDeliveryWorkflowDelivery::getAddress, PecDeliveryWorkflowDelivery::setAddress);
                    });
                }
            }
            case AnalogDeliveryWorkflowFailureLegalFact m ->
                    escape(m.getRecipient(), AnalogDeliveryWorkflowFailureRecipient::getDenomination,
                            AnalogDeliveryWorkflowFailureRecipient::setDenomination);
            case AnalogDeliveryWorkflowTimeoutLegalFact m -> {
                escape(m.getRecipient(), AnalogDeliveryWorkflowTimeoutRecipient::getDenomination,
                        AnalogDeliveryWorkflowTimeoutRecipient::setDenomination);
                escape(m.getRecipient(), AnalogDeliveryWorkflowTimeoutRecipient::getPhysicalAddress,
                        AnalogDeliveryWorkflowTimeoutRecipient::setPhysicalAddress);
            }
            case NotificationViewedLegalFact m -> {
                escape(m.getRecipient(), NotificationViewedRecipient::getDenomination, NotificationViewedRecipient::setDenomination);
                escape(m.getDelegate(), NotificationViewedDelegate::getDenomination, NotificationViewedDelegate::setDenomination);
            }
            case AnalogFeedbackAvailabilityStatement m ->
                    escape(m, AnalogFeedbackAvailabilityStatement::getSenderDenomination,
                            AnalogFeedbackAvailabilityStatement::setSenderDenomination);
            case NotificationCceForEmail m ->
                    escape(m, NotificationCceForEmail::getDenomination, NotificationCceForEmail::setDenomination);
            case InformalCommunication m -> {
                escape(m, InformalCommunication::getSubject, InformalCommunication::setSubject);
                escape(m.getSender(), InformalCommunicationSender::getDenomination, InformalCommunicationSender::setDenomination);
                escape(m.getSender(), InformalCommunicationSender::getService, InformalCommunicationSender::setService);
                escape(m.getRecipient(), SharedInformalCommunicationRecipient::getDenomination, SharedInformalCommunicationRecipient::setDenomination);
            }
            case null, default -> log.debug("No HTML escaping declared for template={}", template);
        }
        return Mono.empty();
    }

    /**
     * Converte in entity HTML i caratteri che il renderer interpreterebbe come markup,
     * delegando all'escaper nativo di FreeMarker: viene usato lo stesso {@link HTMLOutputFormat}
     * applicato dal built-in {@code ?esc}, così il risultato è identico a quello che FreeMarker
     * produrrebbe con l'auto-escaping attivo.
     *
     * @param input la stringa da escapare
     * @return la stringa escapata, oppure l'input invariato se {@code null} o vuoto
     */
    public static String escapeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return HTMLOutputFormat.INSTANCE.escapePlainText(input);
    }

    /**
     * Escapa in-place un singolo campo, ignorando i target nulli.
     */
    private static <T> void escape(T target, Function<T, String> getter, BiConsumer<T, String> setter) {
        if (target == null) {
            return;
        }
        String value = getter.apply(target);
        String escaped = escapeHtml(value);
        if (escaped != null && !escaped.equals(value)) {
            setter.accept(target, escaped);
        }
    }

    /**
     * Naviga un campo annidato gestendo i rami null.
     */
    private static <A, B> B nav(A target, Function<A, B> accessor) {
        return target == null ? null : accessor.apply(target);
    }
}
