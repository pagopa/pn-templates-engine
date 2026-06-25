package it.pagopa.pn.templatesengine.component.impl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.exceptions.DocumentCompositionException;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = {freemarker.template.Configuration.class, TemplateConfig.class})
class DocumentCompositionImplTest {

    private static final String TEMPLATE_NAME = "email_test.html";
    private static final String GENERATED_TEMPLATE_NAME = "email_test_generated.html";
    public static final String TEMPLATES_ASSETS = "templates-assets";

    @Autowired
    Configuration freemarkerConfig;
    @MockitoBean
    TemplateConfig templateConfig;

    DocumentCompositionImpl documentComposition;

    @BeforeEach
    public void setup() {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("templates-assets/" + TEMPLATE_NAME)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found:");
            }
            String templateContent = new String(inputStream.readAllBytes());
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate(TEMPLATE_NAME, templateContent);
            stringLoader.putTemplate(GENERATED_TEMPLATE_NAME, "Hello ${model.name}! ${GENERATED.greeting}");
            freemarkerConfig.setTemplateLoader(stringLoader);
            DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_31);
            owb.setMethodAppearanceFineTuner((in, out) -> out.setMethodShadowsProperty(false));
            freemarkerConfig.setObjectWrapper(owb.build());
            documentComposition = new DocumentCompositionImpl(freemarkerConfig, templateConfig);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void executePdfTemplate() {
        //ARRANGE
        Mockito.when(templateConfig.getTemplatesPath()).thenReturn(TEMPLATES_ASSETS);
        TestModel testModel = new TestModel();
        testModel.setName("Rossi");
        Map<String, Object> model = new HashMap<>();
        model.put("model", testModel);
        //ACT - ASSERT
        var result = Assertions.assertDoesNotThrow(() -> documentComposition.executePdfTemplate(TEMPLATE_NAME, model, null));
        Assertions.assertTrue(result.length > 0);
    }

    @Test
    void executeTxtTemplate() {
        // ARRANGE
        Mockito.when(templateConfig.getTemplatesPath()).thenReturn(TEMPLATES_ASSETS);
        TestModel testModel = new TestModel();
        testModel.setName("Rossi");
        Map<String, Object> model = new HashMap<>();
        model.put("model", testModel);
        // ACT - ASSERT
        String result = Assertions.assertDoesNotThrow(() -> documentComposition.executeTextTemplate(TEMPLATE_NAME, model, null));
        Assertions.assertTrue(result.contains(testModel.name));
    }

    @Test
    void executeTxtTemplate_WithProcessedParamsExposesGeneratedVariable() {
        // ARRANGE
        TestModel testModel = new TestModel();
        testModel.setName("Rossi");
        Map<String, Object> model = new HashMap<>();
        model.put("model", testModel);
        Map<String, String> processedParams = Map.of("greeting", "Benvenuto");

        // ACT
        String result = Assertions.assertDoesNotThrow(() ->
                documentComposition.executeTextTemplate(GENERATED_TEMPLATE_NAME, model, processedParams));

        // ASSERT
        Assertions.assertEquals("Hello Rossi! Benvenuto", result);
    }

    @Test
    void processTemplate_shouldThrowDocumentCompositionExceptionOnTemplateException() {
        // ARRANGE - ACT - ASSERT
        DocumentCompositionException  thrown = Assertions.assertThrows(DocumentCompositionException.class, () ->
                documentComposition.executeTextTemplate("email_test", new HashMap<>(), null)
        );
        Assertions.assertEquals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION.getMessage(), thrown.getMessage());
        Assertions.assertTrue(thrown.getProblem().getDetail().contains("Template not found for name \"email_test\""));
    }

    @Test
    void processTemplate_shouldThrowDocumentCompositionExceptionOnIOException() {
        // ACT - ASSERT
        DocumentCompositionException thrown = Assertions.assertThrows(DocumentCompositionException.class, () ->
                documentComposition.executePdfTemplate(TEMPLATE_NAME, new HashMap<>(), null)
        );
        Assertions.assertEquals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION.getMessage(), thrown.getMessage());
        Assertions.assertTrue(thrown.getProblem().getDetail().contains("The following has evaluated to null or missing:"));
    }

    @Getter
    @Setter
    public static class TestModel {
        public String name;
    }
}