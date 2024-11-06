package it.pagopa.pn.templates.engine.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Data
@Configuration
@ConfigurationProperties(prefix = "templates")
public class PnTemplateConfig {

    private String templatePath;

    private Map<String, Template> templates;

    @Setter
    @Getter
    public static class Template {
        private Map<String, String> input;
    }
}