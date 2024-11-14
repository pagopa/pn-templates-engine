package it.pagopa.pn.templates.engine.config;

import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import it.pagopa.pn.templates.engine.utils.TemplateUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@Data
@Configuration
@ConfigurationProperties
public class TemplateConfig {

    private String templatesPath;
    private String templatesImagesPath;
    private String templatesLogo;
    private Map<String, Template> templatesAsString = new HashMap<>();
    private Map<String, Template> templates;

    @Setter
    @Getter
    public static class Template {
        private boolean loadAsString = false;
        private Map<String, String> input = new HashMap<>();
    }

    @PostConstruct
    public void verifyTemplates() {
        for (String templateName : templates.keySet()) {
            boolean isTemplatePresent = Arrays.stream(TemplatesEnum.values())
                    .anyMatch(templateEnum -> templateEnum.getTemplate().equals(templateName));
            if (!isTemplatePresent) {
                throw new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND,
                        ExceptionTypeEnum.TEMPLATE_NOT_FOUND.getMessage() + templateName);
            }
        }
    }

    @PostConstruct
    public void initializeTemplatesAsString() {
        templates.forEach((templateKey, template) -> {
            var input = template.getInput();
            if (template.isLoadAsString()) {
                var inputAsString = new HashMap<String, String>();
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
}