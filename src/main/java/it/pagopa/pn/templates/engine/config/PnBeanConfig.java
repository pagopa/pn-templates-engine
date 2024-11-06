package it.pagopa.pn.templates.engine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@Data
@Configuration
public class PnBeanConfig {

    @Autowired
    private PnTemplateConfig pnTemplateConfig;

    @Bean
    public FreeMarkerConfigurationFactoryBean freemarkerConfig() {
        FreeMarkerConfigurationFactoryBean factoryBean = new FreeMarkerConfigurationFactoryBean();
        factoryBean.setTemplateLoaderPath(pnTemplateConfig.getTemplatePath());
        return factoryBean;
    }
}