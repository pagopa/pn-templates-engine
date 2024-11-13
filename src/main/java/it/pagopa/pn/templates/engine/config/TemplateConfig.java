package it.pagopa.pn.templates.engine.config;

import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;


@Data
@Configuration
@ConfigurationProperties
public class TemplateConfig {

    private String templatesPath;
    private String templatesImagesPath;
    private String templatesLogo;
    private Map<String, Template> templates;

    @Setter
    @Getter
    public static class Template {
        private Map<String, String> input;
    }

    @PostConstruct
    public void verifyTemplates() {
        for (TemplatesEnum templateEnum : TemplatesEnum.values()) {
            String templateName = templateEnum.getTemplate();
            if (!templates.containsKey(templateName)) {
                throw new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND,
                        ExceptionTypeEnum.TEMPLATE_NOT_FOUND.getMessage() + templateName);
            }
        }
    }
}