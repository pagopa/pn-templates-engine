package it.pagopa.pn.templatesengine.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import it.pagopa.pn.templatesengine.utils.JacksonUtils;
import it.pagopa.pn.templatesengine.utils.TemplateUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configurazione per la gestione dei template dell'applicazione.
 * Definisce le proprietà di configurazione dei percorsi e dei template e
 * include metodi di verifica e inizializzazione per caricare i template.
 */
@Data
@Configuration
@ConfigurationProperties
@Slf4j
public class TemplateConfig {

    private String templatesPath;
    private Map<TemplatesEnum, Template> templatesAsString = new EnumMap<>(TemplatesEnum.class);
    private Map<TemplatesEnum, Template> templates;
    private LanguageEnum defaultLanguage;
    private Map<String, String> templatesStaticParams;

    @Setter
    @Getter
    public static class Template {
        private boolean loadAsString = false;
        private Map<LanguageEnum, String> input = new HashMap<>();
        // Il controllo sulla chiava inserita nella mappa resolvers
        // viene effettuato direttamente da spring
        private Map<TemplatesParamsEnum, Resolver> resolvers;
    }

    @Setter
    @Getter
    @ToString
    public static class Resolver {
        private boolean enabled = false;
        private boolean bypassAllWithNull = false;
        private boolean returnNullOnError = true;
        private boolean whitelistEnabled = false;
        private List<String> whitelistParameterStores;
    }

    /**
     * Verifica all'avvio che tutti i template definiti nella configurazione
     * esistano nella `TemplatesEnum`. Solleva un'eccezione se un template non è presente.
     *
     * @throws PnGenericException se un template definito in configurazione non è trovato in `TemplatesEnum`.
     */
    @PostConstruct
    public void verifyTemplates() {
        Set<String> enumValues = Arrays.stream(TemplatesEnum.values())
                .map(TemplatesEnum::name)
                .collect(Collectors.toSet());

        Set<String> yamlKeys = templates.keySet().stream()
                .map(TemplatesEnum::name)
                .collect(Collectors.toSet());

        log.info("Defined templates: {}", yamlKeys);

        for (String enumValue : enumValues) {
            if (!yamlKeys.contains(enumValue)) {
                throw new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND_FOR_LANGUAGE, enumValue, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        log.info("All defined templates have been found!");
    }

    /**
     * Inizializza i template da caricare come stringhe. Per ogni template definito con
     * `loadAsString = true`, carica il contenuto del file specificato e lo aggiunge a `templatesAsString`.
     */
    @PostConstruct
    public void initializeTemplatesAsString() {
        templates.forEach((templateKey, template) -> {
            var input = template.getInput();
            if (template.isLoadAsString()) {
                Map<LanguageEnum, String> inputAsString = new HashMap<>();
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

    @PostConstruct
    public void printConfig() throws JsonProcessingException {
        Map<String, Object> config = Map.of(
                "templatesPath", templatesPath,
                "defaultLanguage", defaultLanguage,
                "templates", templates
        );
        var sb = new StringBuilder();
        sb.append("\n************************* TEMPLATES CONFIG *************************\n");
        try {
            sb.append(JacksonUtils.ObjectToYamlString(config));
        } catch (JsonProcessingException e) {
            sb.append("ERROR: Unable to convert config to YAML\n");
        }
        sb.append("*********************************************************************");
        log.info(sb.toString());
    }

}