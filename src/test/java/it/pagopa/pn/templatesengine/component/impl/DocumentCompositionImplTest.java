package it.pagopa.pn.templatesengine.component.impl;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = {freemarker.template.Configuration.class, TemplateConfig.class})
class DocumentCompositionImplTest {

    private static final String TEMPLATE_NAME = "email_test.html";
    private static final String TEMPLATE_CONTENT_OUTPUT = "Generated Text from Template";
    public static final String TEMPLATES_ASSETS = "templates-assets";

    @MockBean
    Configuration freemarkerConfig;
    @MockBean
    TemplateConfig templateConfig;
    @Mock
    Template template;

    DocumentCompositionImpl documentComposition;

    @BeforeEach
    public void setup() {
        documentComposition = new DocumentCompositionImpl(freemarkerConfig, templateConfig);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(TEMPLATE_NAME, TEMPLATE_CONTENT_OUTPUT);
    }

    @Test
    void executePdfTemplate() throws IOException {
        //ARRANGE
        Mockito.when(templateConfig.getTemplatesPath()).thenReturn(TEMPLATES_ASSETS);
        Mockito.when(freemarkerConfig.getTemplate(TEMPLATE_NAME)).thenReturn(template);
        Object mapTemplateModel = new Object();

        //ACT - ASSERT
        var result = Assertions.assertDoesNotThrow(() -> documentComposition.executePdfTemplate(TEMPLATE_NAME, mapTemplateModel));
        Assertions.assertTrue(result.length > 0);
    }

    @Test
    void executeTxtTemplate() throws IOException, TemplateException {
        // ARRANGE
        Mockito.when(freemarkerConfig.getTemplate(TEMPLATE_NAME)).thenReturn(template);
        Map<String, String> mapTemplateModel = new HashMap<>();
        mapTemplateModel.put("key", "value");

        // Mock del comportamento di template.process()
        Mockito.doAnswer(invocation -> {
            StringWriter writer = invocation.getArgument(1);
            writer.write(TEMPLATE_CONTENT_OUTPUT);
            return null;
        }).when(template).process(Mockito.eq(mapTemplateModel), Mockito.any(StringWriter.class));

        // ACT - ASSERT
        String result = Assertions.assertDoesNotThrow(() -> documentComposition.executeTextTemplate(TEMPLATE_NAME, mapTemplateModel));
        Assertions.assertEquals(TEMPLATE_CONTENT_OUTPUT, result);
    }

    @Test
    void processTemplate_shouldThrowPnGenericExceptionOnTemplateException() throws IOException, TemplateException {
        // ARRANGE
        Mockito.when(freemarkerConfig.getTemplate(TEMPLATE_NAME)).thenReturn(template);
        Mockito.doThrow(new TemplateException("Template processing error", null))
                .when(template).process(Mockito.any(), Mockito.any(StringWriter.class));

        Map<String, Object> mapTemplateModel = new HashMap<>();

        // ACT - ASSERT
        PnGenericException thrown = Assertions.assertThrows(PnGenericException.class, () ->
                documentComposition.executeTextTemplate(TEMPLATE_NAME, mapTemplateModel)
        );
        Assertions.assertEquals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION, thrown.getExceptionType());
    }

    @Test
    void processTemplate_shouldThrowPnGenericExceptionOnIOException() throws IOException {
        // ARRANGE
        Mockito.when(freemarkerConfig.getTemplate(TEMPLATE_NAME)).thenThrow(new IOException("IO error"));

        Map<String, Object> mapTemplateModel = new HashMap<>();

        // ACT - ASSERT
        PnGenericException thrown = Assertions.assertThrows(PnGenericException.class, () ->
                documentComposition.executePdfTemplate(TEMPLATE_NAME, mapTemplateModel)
        );
        Assertions.assertEquals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION, thrown.getExceptionType());
    }
}