package it.pagopa.pn.templates.engine.rest;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = freemarker.template.Configuration.class)
public class TestFreeMarker {

    @Autowired
    public Configuration freemarker;

    private static final String TEMPLATE_NAME_ATTRIBUTE = "testTemplate";
    private static final String TEMPLATE_CONTENT_ATTRIBUTE = "Hello, ${model.name}!";

    private static final String TEMPLATE_NAME_METHOD = "testTemplateMethod";
    private static final String TEMPLATE_CONTENT_METHOD = "Hello, ${model.getName()}!";

    private static final String TEMPLATE_NAME_GENERATED = "testTemplateGenerated";
    private static final String TEMPLATE_CONTENT_GENERATED = "IUN: ${notification.iun}";

    @BeforeEach
    public void setup() {
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(TEMPLATE_NAME_ATTRIBUTE, TEMPLATE_CONTENT_ATTRIBUTE);
        stringLoader.putTemplate(TEMPLATE_NAME_METHOD, TEMPLATE_CONTENT_METHOD);
        stringLoader.putTemplate(TEMPLATE_NAME_GENERATED, TEMPLATE_CONTENT_GENERATED);

        freemarker.setTemplateLoader(stringLoader);

        DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31);
        owb.setMethodAppearanceFineTuner((in, out) -> out.setMethodShadowsProperty(false));
        freemarker.setObjectWrapper(owb.build());
    }

    @Test
    public void testGenerateContentAttribute() throws Exception {
        // Arrange
        TestModel testModel = new TestModel();
        testModel.name = "Mario";
        Map<String, Object> model = new HashMap<>();
        model.put("model", testModel);
        StringWriter stringWriter = new StringWriter();

        // Act
        Template template = freemarker.getTemplate( TEMPLATE_NAME_ATTRIBUTE );
        template.process( model, stringWriter );
        String out = stringWriter.getBuffer().toString();

        // Assert
        assertEquals("Hello, Mario!", out);
        System.out.println(out);
    }

    @Test
    public void testGenerateContentMethod() throws Exception {
        // Arrange
        TestModel testModel = new TestModel();
        testModel.name = "Mario";
        Map<String, Object> model = new HashMap<>();
        model.put("model", testModel);
        StringWriter stringWriter = new StringWriter();

        // Act
        Template template = freemarker.getTemplate( TEMPLATE_NAME_METHOD );
        template.process( model, stringWriter );
        String out = stringWriter.getBuffer().toString();

        // Assert
        assertEquals("Hello, Mario!", out);
        System.out.println(out);
    }

    @Test
    public void testGenerateContentGeneratedModel() throws Exception {
        // Arrange
        Notification notification = new Notification();
        notification.setIun("ABC");

        Map<String, Object> model = new HashMap<>();
        model.put("notification", notification);
        StringWriter stringWriter = new StringWriter();

        // Act
        Template template = freemarker.getTemplate( TEMPLATE_NAME_GENERATED );
        template.process( model, stringWriter );
        String out = stringWriter.getBuffer().toString();

        // Assert
        assertEquals("IUN: ABC", out);
        System.out.println(out);
    }

    public class TestModel{
        private String name;
        public String getName() {
            return name;
        }
    }
}