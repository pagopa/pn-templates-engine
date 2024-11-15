package it.pagopa.pn.templatesengine.config;

import freemarker.cache.ClassTemplateLoader;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PnFreemarkerConfigTest {

    public static final String TEMPLATES_ASSETS = "templates-assets";
    private static final String TEMPLATE_NAME = "email_test.html";
    private static final LanguageEnum LANGUAGE = LanguageEnum.IT;
    public static final String INVALID_PATH = "invalidPath";

    @Mock
    private freemarker.template.Configuration configuration;

    @Mock
    private TemplateConfig templateConfig;

    @InjectMocks
    private PnFreemarkerConfig pnFreemarkerConfig;

    @Test
    void testFreemarkerConfig_Success() {
        // Arrange
        TemplateConfig.Template template = mock(TemplateConfig.Template.class);
        when(template.isLoadAsString()).thenReturn(false);
        Map<String, String> templateInputs = new HashMap<>();
        templateInputs.put(LANGUAGE.getValue(), TEMPLATE_NAME);
        when(template.getInput()).thenReturn(templateInputs);
        when(templateConfig.getTemplates()).thenReturn(Map.of(TemplatesEnum.EMAIL_BODY, template));

        // Act
        freemarker.template.Configuration result = pnFreemarkerConfig.freemarkerConfig(TEMPLATES_ASSETS);

        // Assert
        assertNotNull(result);
        verify(configuration).setTemplateLoader(any(ClassTemplateLoader.class));
    }

    @Test
    void freemarkerConfig_ShouldThrowPnGenericException_WhenConfigurationFails() {
        // Arrange

        doThrow(new RuntimeException("Errore di configurazione"))
                .when(configuration).setTemplateLoader(any(ClassTemplateLoader.class));

        // Act & Assert
        PnGenericException exception = assertThrows(PnGenericException.class, () -> {
            pnFreemarkerConfig.freemarkerConfig(INVALID_PATH);
        });

        Assertions.assertEquals(ExceptionTypeEnum.ERROR_FREEMARKER_BEAN_CONFIGURATION, exception.getExceptionType());
        assertTrue(exception.getMessage().contains("Errore di configurazione"));
    }
}
