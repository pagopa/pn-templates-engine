package it.pagopa.pn.templatesengine.config;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.processor.TemplateProcessorRegistry;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoUrlProcessor;
import it.pagopa.pn.templatesengine.processor.impl.CharNormalizerProcessor;
import it.pagopa.pn.templatesengine.processor.impl.CharNormalizerProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoBase64Processor;
import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import it.pagopa.pn.templatesengine.model.InformalAnalogCommunicationGeneratedParams;
import jakarta.annotation.PostConstruct;


/**
 * Configurazione dei processori per template.
 * Qui si registrano le chain tipizzate con: template → model class → output factory → processori + mapper.
 *
 * <p>I processori vengono eseguiti nell'ordine di registrazione.
 * Il mapper estrae dal model l'oggetto su cui il processore opera.</p>
 */
@Configuration
@RequiredArgsConstructor
public class TemplateProcessorRegistryConfig {

    private final TemplateProcessorRegistry registry;
    private final MarkdownToHtmlProcessor markdownToHtmlProcessor;
    private final SenderLogoUrlProcessor senderLogoUrlProcessor;
    private final SenderLogoBase64Processor senderLogoBase64Processor;
    private final CharNormalizerProcessor charNormalizerProcessor;

    @PostConstruct
    public void init() {
        registry.registerChain(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, InformalCommunication.class, InformalCommunicationGeneratedParams::new)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(senderLogoBase64Processor, model -> model.getSender().getId());

        registry.registerChain(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, InformalCommunication.class, InformalCommunicationGeneratedParams::new)
                .add(charNormalizerProcessor, model -> model)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(senderLogoUrlProcessor, model -> model.getSender().getId());

        registry.registerChain(TemplatesEnum.INFORMAL_PEC_COMMUNICATION_BODY, InformalCommunication.class, InformalCommunicationGeneratedParams::new)
                .add(charNormalizerProcessor, model -> model)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(senderLogoBase64Processor, model -> model.getSender().getId());

    }
}