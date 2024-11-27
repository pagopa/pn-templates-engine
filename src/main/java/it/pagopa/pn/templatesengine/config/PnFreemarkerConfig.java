package it.pagopa.pn.templatesengine.config;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.DefaultObjectWrapperBuilder;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static it.pagopa.pn.templatesengine.utils.TemplateUtils.loadTemplateContent;

@Configuration
@AllArgsConstructor
public class PnFreemarkerConfig {

    private final freemarker.template.Configuration configuration;
    private final TemplateConfig templateConfig;

    /**
     * Configura e restituisce un bean FreeMarker `Configuration` utilizzato per il rendering dei template.
     *
     * @param templatesPath il percorso della directory che contiene i template, ottenuto dalle proprietà applicative.
     * @return un'istanza di `freemarker.template.Configuration` configurata per l'utilizzo con FreeMarker.
     * @throws PnGenericException in caso di errore durante la configurazione del bean FreeMarker.
     */
    @Bean("freemarkerConfig")
    public freemarker.template.Configuration freemarkerConfig(@Value("${templatesPath}") String templatesPath) {
        try {
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);

            //Carica i template come stringa dal file system
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            templateConfig.getTemplates().forEach((templateKey, template) -> {
                Map<String, String> input = template.getInput();
                if (!template.isLoadAsString()) {  //carica il contenuto solo se il template necessita di un body
                    input.forEach((inputKey, templateFile) -> {
                        String templateContent = loadTemplateContent(templatesPath + "/" + templateFile);
                        stringLoader.putTemplate(templateFile, templateContent);
                    });
                }
            });

            //Aggiunge `StringTemplateLoader` alla configurazione di FreeMarker
            configuration.setTemplateLoader(stringLoader);

            //Configura un ObjectWrapper per gestire la visualizzazione di metodi e proprietà sugli oggetti
            DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_31);
            owb.setMethodAppearanceFineTuner((in, out) -> out.setMethodShadowsProperty(false));
            configuration.setObjectWrapper(owb.build());

            return configuration;
        } catch (Exception exception) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_FREEMARKER_BEAN_CONFIGURATION,
                    exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}