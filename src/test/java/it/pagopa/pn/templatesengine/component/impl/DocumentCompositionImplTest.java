package it.pagopa.pn.templatesengine.component.impl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = {freemarker.template.Configuration.class, TemplateConfig.class})
class DocumentCompositionImplTest {

    private static final String TEMPLATE_NAME = "email_test.html";
    public static final String TEMPLATES_ASSETS = "templates-assets";
    public static final String EMAIL_TEST = "email_test";

    @Autowired
    Configuration freemarkerConfig;
    @MockBean
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
            stringLoader.putTemplate(EMAIL_TEST, templateContent);
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
        var result = Assertions.assertDoesNotThrow(() -> documentComposition.executePdfTemplate(EMAIL_TEST, model));
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
        String result = Assertions.assertDoesNotThrow(() -> documentComposition.executeTextTemplate(EMAIL_TEST, model));
        Assertions.assertTrue(result.contains(testModel.name));
    }

    @Test
    void processTemplate_shouldThrowPnGenericExceptionOnTemplateException() {
        // ARRANGE - ACT - ASSERT
        PnGenericException thrown = (PnGenericException)  Assertions.assertThrows(Exception.class, () ->
                documentComposition.executeTextTemplate(TEMPLATE_NAME, new HashMap<>())
        );
        Assertions.assertEquals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION, thrown.getExceptionType());
        Assertions.assertTrue(thrown.getMessage().contains("Template not found for name \"email_test.html\""));
    }

    @Test
    void processTemplate_shouldThrowPnGenericExceptionOnIOException() {

        // ACT - ASSERT
        PnGenericException thrown = (PnGenericException) Assertions.assertThrows(Exception.class, () ->
                documentComposition.executePdfTemplate(TEMPLATE_NAME, new HashMap<>())
        );
        Assertions.assertEquals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION, thrown.getExceptionType());
    }

    @Getter
    @Setter
    public static class TestModel {
        public String name;
    }
}