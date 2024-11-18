package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateConfigTest {

    private static final String LANGUAGE = LanguageEnum.IT.getValue();

    @Autowired
    private TemplateConfig templateConfig;

    @BeforeEach
    void setup() {
        Map<TemplatesEnum, TemplateConfig.Template> templates = new HashMap<>();
        TemplateConfig.Template template = new TemplateConfig.Template();
        template.setLoadAsString(true);
        Map<String, String> input = new HashMap<>();
        input.put(LANGUAGE, "email_test.html");
        template.setInput(input);
        templates.put(TemplatesEnum.EMAIL_SUBJECT, template);
        templateConfig.setTemplates(templates);
        templateConfig.setTemplatesPath("templates-assets");
    }

    @Test
    void verifyTemplates_ShouldThrowException_WhenTemplateNotFoundInEnum() {
        // Arrange
        templateConfig.getTemplates().put(TemplatesEnum.EMAIL_BODY, new TemplateConfig.Template());

        // Act - Assert
        PnGenericException exception = assertThrows(PnGenericException.class, () -> templateConfig.verifyTemplates());
        Assertions.assertEquals(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, exception.getExceptionType());
    }

    @Test
    void initializeTemplatesAsString_ShouldLoadTemplateContent_WhenLoadAsStringIsTrue() {
        // Arrange
        templateConfig.initializeTemplatesAsString();

        // Act - Assert
        TemplateConfig.Template template = templateConfig.getTemplatesAsString().get(TemplatesEnum.PEC_BODY_REJECT);
        assertTrue(template.getInput().containsKey(LANGUAGE));
        assertNotNull(template.getInput().get(LANGUAGE));
    }

}