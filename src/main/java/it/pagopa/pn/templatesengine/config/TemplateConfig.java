package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.utils.TemplateUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Configurazione per la gestione dei template dell'applicazione.
 * Definisce le proprietà di configurazione dei percorsi e dei template e
 * include metodi di verifica e inizializzazione per caricare i template.
 */
@Data
@Configuration
@ConfigurationProperties
public class TemplateConfig {

    private String templatesPath;
    private Map<TemplatesEnum, Template> templatesAsString = new EnumMap<>(TemplatesEnum.class);
    private Map<TemplatesEnum, Template> templates;

    @Setter
    @Getter
    public static class Template {
        private boolean loadAsString = false;
        private Map<String, String> input = new HashMap<>();
    }

    /**
     * Verifica all'avvio che tutti i template definiti nella configurazione
     * esistano nella `TemplatesEnum`. Solleva un'eccezione se un template non è presente.
     *
     * @throws PnGenericException se un template definito in configurazione non è trovato in `TemplatesEnum`.
     */
    @PostConstruct
    public void verifyTemplates() {
        for (TemplatesEnum templateName : templates.keySet()) {
            Arrays.stream(TemplatesEnum.values())
                    .anyMatch(templateEnum -> templateEnum.getTemplate().equals(templateName));
            boolean isTemplatePresent = false;
            // TODO
        }
    }

    /**
     * Inizializza i template da caricare come stringhe. Per ogni template definito con
     * `loadAsString = true`, carica il contenuto del file specificato e lo aggiunge a `templatesAsString`.
     */
    @PostConstruct
    public void initializeTemplatesAsString() {
        templates.forEach((templateKey, template) -> {
            Map<String, String> input = template.getInput();
            if (template.isLoadAsString()) {
                Map<String, String> inputAsString = new HashMap<>();
                input.forEach((inputKey, templateName) -> {
                    String templateContent = TemplateUtils.loadTemplateContent(getTemplatesPath() + "/" + templateName);
                    inputAsString.put(inputKey, templateContent);
                });
                TemplateConfig.Template templatesAsStringEntry = new TemplateConfig.Template();
                templatesAsStringEntry.setInput(inputAsString);
                templatesAsString.put(templateKey, templatesAsStringEntry);
            }
        });
    }
}