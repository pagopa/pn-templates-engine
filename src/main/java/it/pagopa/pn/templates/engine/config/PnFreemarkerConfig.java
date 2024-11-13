package it.pagopa.pn.templates.engine.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.DefaultObjectWrapperBuilder;
import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import it.pagopa.pn.templates.engine.utils.TemplateUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.HashMap;

@Configuration
@AllArgsConstructor
public class PnFreemarkerConfig {

    private final freemarker.template.Configuration configuration;
    private final TemplateConfig templateConfig;

    @Bean("freemarkerConfig")
    public freemarker.template.Configuration freemarkerConfig(@Value("${templatesPath}") String templatePath) {
        try {
            ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(this.getClass().getClassLoader(), "/".concat(templatePath));
            configuration.setTemplateLoader(classTemplateLoader);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            templateConfig.getTemplates().forEach((templateKey, template) -> {
                var input = template.getInput();
                if (template.isLoadAsString()) {
                    var inputAsString = new HashMap<String, String>();
                    input.forEach((inputKey, templateName) -> {
                        String templateContent = TemplateUtils.loadTemplateContent(templatePath + "/" + templateName);
                        inputAsString.put(inputKey, templateContent);
                    });
                    TemplateConfig.Template templatesAsString = new TemplateConfig.Template();
                    templatesAsString.setInput(inputAsString);
                    templateConfig.getTemplatesAsString().put(templateKey, templatesAsString);
                } else {
                    input.forEach((inputKey, templateName) -> {
                        String templateContent = TemplateUtils.loadTemplateContent(templatePath + "/" + templateName);
                        stringLoader.putTemplate(templateName, templateContent);
                    });
                }
            });
            configuration.setTemplateLoader(stringLoader);
            DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_31);
            owb.setMethodAppearanceFineTuner((in, out) -> out.setMethodShadowsProperty(false));
            configuration.setObjectWrapper(owb.build());
            return configuration;
        } catch (Exception exception) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_FREEMARKER_BEAN_CONFIGURATION, exception.getMessage());
        }
    }
}