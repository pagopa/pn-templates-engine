package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class TemplateServiceTest {

    @MockBean
    DocumentComposition documentComposition;
    @MockBean
    TemplateConfig templateConfig;
    TemplateService templateService;

    private static final String TEMPLATE_NAME = "testTemplate";
    private static final LanguageEnum LANGUAGE = LanguageEnum.IT;
    private static final String TEMPLATE_CONTENT = "Template Content";
    private static final byte[] PDF_CONTENT = new byte[]{1, 2, 3, 4};

    @BeforeEach
    public void setup() {
        templateService = new TemplateService(documentComposition, templateConfig);
    }

    @Test
    void executeTextTemplate() {
        // Arrange
        TemplateConfig.Template template = mock(TemplateConfig.Template.class);
        Map<String, String> templateInputs = new HashMap<>();
        templateInputs.put(LANGUAGE.getValue(), TEMPLATE_NAME);
        when(template.getInput()).thenReturn(templateInputs);
        when(templateConfig.getTemplates()).thenReturn(Map.of(TemplatesEnum.EMAIL_BODY, template));
        when(documentComposition.executeTextTemplate(TEMPLATE_NAME, TEMPLATE_CONTENT)).thenReturn(TEMPLATE_CONTENT);

        // Act & Assert
        /*
                StepVerifier.create(templateService.executeTextTemplate(TemplatesEnum.EMAIL_BODY, LANGUAGE, TEMPLATE_CONTENT))
                .expectNext(TEMPLATE_CONTENT)
                .verifyComplete();
         */

    }

    @Test
    void executePdfTemplate_Success() {
        // Arrange
        TemplateConfig.Template template = mock(TemplateConfig.Template.class);
        Map<String, String> templateInputs = new HashMap<>();
        templateInputs.put(LANGUAGE.getValue(), TEMPLATE_NAME);
        when(template.getInput()).thenReturn(templateInputs);

        when(templateConfig.getTemplates()).thenReturn(Map.of(TemplatesEnum.EMAIL_BODY, template));
        when(documentComposition.executePdfTemplate(TEMPLATE_NAME, TEMPLATE_CONTENT)).thenReturn(PDF_CONTENT);

        // Act & Assert
        /*
                StepVerifier.create(templateService.executePdfTemplate(TemplatesEnum.EMAIL_BODY, LANGUAGE, TEMPLATE_CONTENT))
                .expectNext(PDF_CONTENT)
                .verifyComplete();
         */

    }

    @Test
    void executeTextTemplateAsString_Success() {
        // Arrange
        TemplateConfig.Template template = mock(TemplateConfig.Template.class);
        Map<String, String> templateInputs = new HashMap<>();
        templateInputs.put(LANGUAGE.getValue(), TEMPLATE_CONTENT);
        when(template.getInput()).thenReturn(templateInputs);

        when(templateConfig.getTemplatesAsString()).thenReturn(Map.of(TemplatesEnum.EMAIL_BODY, template));

        // Act & Assert
        StepVerifier.create(templateService.executeTextTemplate(TemplatesEnum.EMAIL_BODY, LANGUAGE))
                .expectNext(TEMPLATE_CONTENT)
                .verifyComplete();
    }

    @Test
    void executePdfTemplate_TemplateNotFound() {
        // Arrange
        TemplateConfig.Template template = mock(TemplateConfig.Template.class);
        Map<String, String> templateInputs = new HashMap<>();
        templateInputs.put(LANGUAGE.getValue(), TEMPLATE_NAME);
        when(template.getInput()).thenReturn(templateInputs);
        when(templateConfig.getTemplates()).thenReturn(Map.of(TemplatesEnum.EMAIL_BODY, template));
        when(documentComposition.executeTextTemplate(TEMPLATE_NAME, TEMPLATE_CONTENT)).thenReturn("");

        // Act & Assert
        /*
                StepVerifier.create(templateService.executePdfTemplate(TemplatesEnum.EMAIL_BODY, LANGUAGE, TEMPLATE_CONTENT))
                .expectErrorMatches(throwable -> throwable instanceof PnGenericException &&
                        ((PnGenericException) throwable).getExceptionType() == ExceptionTypeEnum.TEMPLATE_NOT_FOUND &&
                        throwable.getMessage().contains(TEMPLATE_NAME))
                .verify();
         */

    }

    @Test
    void executeTXTTemplate_TemplateNotFound() {
        // Arrange
        TemplateConfig.Template template = mock(TemplateConfig.Template.class);
        Map<String, String> templateInputs = new HashMap<>();
        templateInputs.put(LANGUAGE.getValue(), TEMPLATE_NAME);
        when(template.getInput()).thenReturn(templateInputs);
        when(templateConfig.getTemplates()).thenReturn(Map.of(TemplatesEnum.EMAIL_BODY, template));

        // Act & Assert
        /*
                StepVerifier.create(templateService.executeTextTemplate(TemplatesEnum.EMAIL_BODY, LANGUAGE, TEMPLATE_CONTENT))
                .expectErrorMatches(throwable -> throwable instanceof PnGenericException &&
                        ((PnGenericException) throwable).getExceptionType() == ExceptionTypeEnum.TEMPLATE_NOT_FOUND &&
                        throwable.getMessage().contains(TEMPLATE_NAME))
                .verify();
         */

    }
}