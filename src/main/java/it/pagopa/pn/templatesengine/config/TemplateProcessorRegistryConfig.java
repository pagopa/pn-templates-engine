package it.pagopa.pn.templatesengine.config;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.processor.TemplateProcessorRegistry;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoUrlProcessor;
import it.pagopa.pn.templatesengine.processor.impl.CharNormalizerProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templatesengine.processor.impl.HtmlEscapeProcessor;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoBase64Processor;
import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import jakarta.annotation.PostConstruct;


/**
 * Configurazione dei processori per template.
 * Qui si registrano le chain tipizzate con: template → model class → output factory → processori + mapper.
 *
 * <p>I processori vengono eseguiti nell'ordine di registrazione.
 * Il mapper estrae dal model l'oggetto su cui il processore opera.</p>
 *
 * <p>{@link HtmlEscapeProcessor} è registrato per primo sui template HTML che ricevono testo libero
 * valorizzato dall'ente, così da operare sul valore originale e non sull'output degli altri processor,
 * che è già HTML e non va escapato. I template di solo testo (SMS, subject) non lo registrano.</p>
 */
@Configuration
@RequiredArgsConstructor
public class TemplateProcessorRegistryConfig {

    private final TemplateProcessorRegistry registry;
    private final MarkdownToHtmlProcessor markdownToHtmlProcessor;
    private final SenderLogoUrlProcessor senderLogoUrlProcessor;
    private final SenderLogoBase64Processor senderLogoBase64Processor;
    private final CharNormalizerProcessor charNormalizerProcessor;
    private final HtmlEscapeProcessor htmlEscapeProcessor;

    @PostConstruct
    public void init() {
        registerHtmlEscapeOnlyChains();
        registerInformalCommunicationChains();
    }

    /**
     * Template HTML che non producono parametri generati: l'unico step è l'escaping dei campi testuali.
     */
    private void registerHtmlEscapeOnlyChains() {
        registry.registerChain(TemplatesEnum.NOTIFICATION_AAR, NotificationAar.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_AAR_RADDALT, NotificationAarRaddAlt.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_AAR_FOR_PEC, NotificationAarForPec.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL_ANALOG, NotificationAarForEmailAnalog.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL_DIGITAL, NotificationAarForEmailDigital.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, NotificationReceivedLegalFact.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT, NotificationCancelledLegalFact.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, PecDeliveryWorkflowLegalFact.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, AnalogDeliveryWorkflowFailureLegalFact.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_TIMEOUT_LEGAL_FACT, AnalogDeliveryWorkflowTimeoutLegalFact.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT, NotificationViewedLegalFact.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.ANALOG_FEEDBACK_AVAILABILITY_STATEMENT, AnalogFeedbackAvailabilityStatement.class)
                .add(htmlEscapeProcessor, model -> model);

        registry.registerChain(TemplatesEnum.NOTIFICATION_CCE_FOR_EMAIL, NotificationCceForEmail.class)
                .add(htmlEscapeProcessor, model -> model);
    }

    private void registerInformalCommunicationChains() {
        registry.registerChain(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, InformalCommunication.class, InformalCommunicationGeneratedParams::new)
                .add(htmlEscapeProcessor, model -> model)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(senderLogoBase64Processor, model -> model.getSender().getId());

        registry.registerChain(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, InformalCommunication.class, InformalCommunicationGeneratedParams::new)
                .add(htmlEscapeProcessor, model -> model)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(charNormalizerProcessor, model -> model)
                .add(senderLogoUrlProcessor, model -> model.getSender().getId());

        registry.registerChain(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, InformalCommunication.class, InformalCommunicationGeneratedParams::new)
                .add(htmlEscapeProcessor, model -> model)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(charNormalizerProcessor, model -> model)
                .add(senderLogoBase64Processor, model -> model.getSender().getId());
    }
}
