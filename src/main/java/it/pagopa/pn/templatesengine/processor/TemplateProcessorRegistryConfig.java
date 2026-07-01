package it.pagopa.pn.templatesengine.processor;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.processor.impl.InformalSenderLogoProcessor;
import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import it.pagopa.pn.templatesengine.model.params.InformalAnalogCommunicationGeneratedParams;
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
    private final InformalSenderLogoProcessor informalSenderLogoProcessor;

    @PostConstruct
    public void init() {
        registry.registerChain(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, InformalCommunication.class, InformalAnalogCommunicationGeneratedParams::new)
                .add(markdownToHtmlProcessor, InformalCommunication::getBody)
                .add(informalSenderLogoProcessor, model -> model.getSender().getPaId());
    }
}