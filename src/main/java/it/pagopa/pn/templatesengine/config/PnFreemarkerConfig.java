package it.pagopa.pn.templatesengine.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.DefaultObjectWrapperBuilder;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.utils.TemplateUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class PnFreemarkerConfig {

    private final freemarker.template.Configuration configuration;
    private final TemplateConfig templateConfig;

    /**
     * Configura e restituisce un bean FreeMarker `Configuration` utilizzato per il rendering dei template.
     * Questo metodo imposta il caricamento dei template da due sorgenti:
     * - `ClassTemplateLoader` per caricare i template da file nella directory specificata.
     * - `StringTemplateLoader` per caricare i template definiti come stringhe in base alla configurazione.
     *
     * @param templatePath il percorso della directory che contiene i template, ottenuto dalle proprietà applicative.
     * @return un'istanza di `freemarker.template.Configuration` configurata per l'utilizzo con FreeMarker.
     * @throws PnGenericException in caso di errore durante la configurazione del bean FreeMarker.
     */
    @Bean("freemarkerConfig")
    public freemarker.template.Configuration freemarkerConfig(@Value("${templatesPath}") String templatePath) {
        try {
            //Caricatore di template basato su percorso di classe, per accedere ai file in `templatePath`
            ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(this.getClass().getClassLoader(), "/".concat(templatePath));


            //Configurazione principale di FreeMarker
            configuration.setTemplateLoader(classTemplateLoader);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);

            //Carica i template come stringa dal file system
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            templateConfig.getTemplates().forEach((templateKey, template) -> {
                var input = template.getInput();
                if (!template.isLoadAsString()) {  //carica il contenuto solo se il template necessita di un body
                    input.forEach((inputKey, templateName) -> {
                        String templateContent = TemplateUtils.loadTemplateContent(templatePath + "/" + templateName);
                        stringLoader.putTemplate(templateName, templateContent);
                    });
                }
            });

            //Aggiunge `StringTemplateLoader` alla configurazione di FreeMarke
            configuration.setTemplateLoader(stringLoader);

            //Configura un ObjectWrapper per gestire la visualizzazione di metodi e proprietà sugli oggetti
            DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_31);
            owb.setMethodAppearanceFineTuner((in, out) -> out.setMethodShadowsProperty(false));
            configuration.setObjectWrapper(owb.build());

            return configuration;
        } catch (Exception exception) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_FREEMARKER_BEAN_CONFIGURATION, exception.getMessage());
        }
    }
}