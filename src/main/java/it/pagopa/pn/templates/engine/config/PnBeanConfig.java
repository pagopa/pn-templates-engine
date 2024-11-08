package it.pagopa.pn.templates.engine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import it.pagopa.pn.templates.engine.exceptions.*;


@Data
@Configuration
public class PnBeanConfig {

    @Bean
    public freemarker.template.Configuration freemarkerConfig(@Value("${templates.templatePath}") String templatePath) {
        try {
            FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
            factoryBean.setTemplateLoaderPath("classpath:" + templatePath);
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        } catch (Exception exception) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_FREEMARKER_BEAN_CONFIGURATION, exception.getMessage());
        }
    }
}