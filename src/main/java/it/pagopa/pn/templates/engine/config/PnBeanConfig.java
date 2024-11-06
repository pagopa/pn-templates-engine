package it.pagopa.pn.templates.engine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Data
@Configuration
public class PnBeanConfig {

    @Bean
    public FreeMarkerConfigurationFactoryBean freemarkerConfig(@Value("${templates.templatePath}") String templatePath) {
        FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
        factoryBean.setTemplateLoaderPath("classpath:" + templatePath);
        return factoryBean;
    }
}