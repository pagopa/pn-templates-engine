package it.pagopa.pn.templatesengine.service;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.component.impl.DocumentCompositionImpl;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.Emailbody;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = {freemarker.template.Configuration.class})
class TemplateServiceTest {

    @Autowired
    Configuration freemarkerConfig;
    TemplateService templateService;
    DocumentComposition documentComposition;
    @MockBean
    TemplateConfig templateConfig;

    public static final String TEMPLATE_FILE_HTML = "Mail_VerificationCodeBody.html";
    private static final LanguageEnum LANGUAGE = LanguageEnum.IT;
    private static final String TEMPLATE_PATH = "templates-assets";

    @BeforeEach
    public void setup() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(TEMPLATE_PATH + "/" + TEMPLATE_FILE_HTML)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found:");
            }
            String templateContent = new String(inputStream.readAllBytes());
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            stringLoader.putTemplate(TEMPLATE_FILE_HTML, templateContent);
            freemarkerConfig.setTemplateLoader(stringLoader);
            DefaultObjectWrapperBuilder owb = new DefaultObjectWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_31);
            owb.setMethodAppearanceFineTuner((in, out) -> out.setMethodShadowsProperty(false));
            freemarkerConfig.setObjectWrapper(owb.build());

            templateConfig = new TemplateConfig();
            templateConfig.setTemplatesPath(TEMPLATE_PATH);
            //setInputs
            Map<String, String> inputs = new HashMap<>();
            inputs.put(LANGUAGE.getValue(), TEMPLATE_FILE_HTML);
            TemplateConfig.Template template = new TemplateConfig.Template();
            template.setInput(inputs);

            Map<TemplatesEnum, TemplateConfig.Template> templates = new HashMap<>();
            templates.put(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, template);
            templateConfig.setTemplates(templates);
            templateConfig.setTemplatesAsString(templates);
        }
    }

    @Test
    void executeTextTemplate() {
        // Arrange
        documentComposition = new DocumentCompositionImpl(freemarkerConfig, templateConfig);
        templateService = new TemplateService(documentComposition, templateConfig);
        Emailbody emailbody = new Emailbody();
        emailbody.setVerificationCode("VerificationCode");

        // Act & Assert
        Mono<byte[]> result = Assertions.assertDoesNotThrow(() ->
                templateService.executePdfTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, LANGUAGE, Mono.just(emailbody)));

        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void executePdfTemplate_Success() {
        // Arrange
        documentComposition = new DocumentCompositionImpl(freemarkerConfig, templateConfig);
        templateService = new TemplateService(documentComposition, templateConfig);
        Emailbody emailbody = new Emailbody();
        emailbody.setVerificationCode("VerificationCode");

        // Act & Assert
        Mono<String> result = Assertions.assertDoesNotThrow(() ->
                templateService.executeTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, LANGUAGE, Mono.just(emailbody)));

        StepVerifier.create(result)
                .assertNext(actualResult -> {
                    Assertions.assertTrue(actualResult.contains("VerificationCode"),
                            "Result should include the verification code");
                })
                .verifyComplete();
    }

    @Test
    void executeTextTemplateAsString_Success() {
        documentComposition = new DocumentCompositionImpl(freemarkerConfig, templateConfig);
        templateService = new TemplateService(documentComposition, templateConfig);
        // Act & Assert
        StepVerifier.create(templateService.executeTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, LANGUAGE))
                .expectNext(TEMPLATE_FILE_HTML)
                .verifyComplete();
    }

    @Test
    void executePdfTemplate_ShouldFailWhenDocumentCompositionThrowsException() {
        // Arrange
        documentComposition = Mockito.mock(DocumentComposition.class);
        templateService = new TemplateService(documentComposition, templateConfig);
        Emailbody emailbody = new Emailbody();
        emailbody.setVerificationCode("VerificationCode");

        Mockito.when(documentComposition.executePdfTemplate(Mockito.anyString(), Mockito.any()))
                .thenThrow(new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION,
                        "Non è stato possibile elaborare il pdf", HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        Mono<byte[]> result = templateService.executePdfTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, LANGUAGE, Mono.just(emailbody));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof PnGenericException &&
                                throwable.getMessage().contains("Non è stato possibile elaborare il pdf")
                )
                .verify();
    }

    @Test
    void executeTxtTemplate_EmptyResult() {
        // Arrange
        documentComposition = Mockito.mock(DocumentComposition.class);
        templateService = new TemplateService(documentComposition, templateConfig);
        Emailbody emailbody = new Emailbody();
        emailbody.setVerificationCode("VerificationCode");

        Mockito.when(documentComposition.executeTextTemplate(Mockito.anyString(), Mockito.any()))
                .thenThrow(new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION,
                        "Non è stato possibile elaborare il template", HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        Mono<String> result = templateService.executeTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, LANGUAGE, Mono.just(emailbody));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof PnGenericException &&
                                throwable.getMessage().contains("Non è stato possibile elaborare il template")
                        && ((PnGenericException) throwable).getExceptionType().equals(ExceptionTypeEnum.ERROR_TEMPLATES_DOCUMENT_COMPOSITION)
                )
                .verify();
    }

    @Getter
    @Setter
    public static class TestModel {
        public String name;
    }
}