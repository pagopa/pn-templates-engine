package it.pagopa.pn.templatesengine.processor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

/**
 * Configurazione dei processori per template.
 * Qui si registrano le associazioni TemplatesEnum → TemplateModelProcessor + mapper.
 *
 * <p>I processori vengono eseguiti nell'ordine di registrazione.
 * Il mapper estrae dal model l'oggetto su cui il processore opera.</p>
 */
@Configuration
@RequiredArgsConstructor
public class TemplateProcessorRegistryConfig {

    public TemplateProcessorRegistryConfig(TemplateProcessorRegistry registry) {

    }
}