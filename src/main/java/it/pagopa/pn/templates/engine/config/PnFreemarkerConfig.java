package it.pagopa.pn.templates.engine.config;

import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class PnFreemarkerConfig {

    private final freemarker.template.Configuration configuration;

    @Bean
    public freemarker.template.Configuration freemarkerConfig(@Value("${templatesPath}") String templatePath) {
        try {
            configuration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "/".concat(templatePath));
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLogTemplateExceptions(false);
            configuration.setWrapUncheckedExceptions(true);
            return configuration;
        } catch (Exception exception) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_FREEMARKER_BEAN_CONFIGURATION, exception.getMessage());
        }
    }
}