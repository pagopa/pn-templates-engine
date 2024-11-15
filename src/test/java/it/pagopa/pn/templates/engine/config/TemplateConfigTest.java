package it.pagopa.pn.templates.engine.config;

import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.LanguageEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateConfigTest {

    private static final String EXISTING_TEMPLATE = "existingTemplate";
    private static final String LANGUAGE = LanguageEnum.IT.getValue();

    @Autowired
    private TemplateConfig templateConfig;

    @BeforeEach
    void setup() {
        Map<String, TemplateConfig.Template> templates = new HashMap<>();
        TemplateConfig.Template template = new TemplateConfig.Template();
        template.setLoadAsString(true);
        Map<String, String> input = new HashMap<>();
        input.put(LANGUAGE, "email_test.html");
        template.setInput(input);
        templates.put(EXISTING_TEMPLATE, template);
        templateConfig.setTemplates(templates);
        templateConfig.setTemplatesPath("templates-assets");
    }

    @Test
    void verifyTemplates_ShouldThrowException_WhenTemplateNotFoundInEnum() {
        // Arrange
        templateConfig.getTemplates().put("nonExistentTemplate", new TemplateConfig.Template());

        // Act - Assert
        PnGenericException exception = assertThrows(PnGenericException.class, () -> templateConfig.verifyTemplates());
        assertEquals(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, exception.getExceptionType());
    }

    @Test
    void initializeTemplatesAsString_ShouldLoadTemplateContent_WhenLoadAsStringIsTrue() {
        // Arrange
        templateConfig.initializeTemplatesAsString();

        // Act - Assert
        TemplateConfig.Template template = templateConfig.getTemplatesAsString().get(EXISTING_TEMPLATE);
        assertTrue(template.getInput().containsKey(LANGUAGE));
        assertNotNull(template.getInput().get(LANGUAGE));
    }

}