package it.pagopa.pn.templatesengine.service;

import freemarker.template.Configuration;
import it.pagopa.pn.templatesengine.component.DocumentComposition;
import it.pagopa.pn.templatesengine.component.impl.DocumentCompositionImpl;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.MailVerificationCodeBody;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.MalfunctionLegalFact;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


@SpringBootTest
class TemplateServiceTest {

    @Autowired
    Configuration freemarkerConfig;
    @Autowired
    TemplateConfig templateConfig;
    TemplateService templateService;
    DocumentComposition documentComposition;

    private static final LanguageEnum LANGUAGE = LanguageEnum.IT;

    @BeforeEach
    public void setup() {
        documentComposition = new DocumentCompositionImpl(freemarkerConfig, templateConfig);
        templateService = new TemplateService(documentComposition, templateConfig);
    }

    @Test
    void executeTextTemplate_Success() {
        // Arrange
        MailVerificationCodeBody emailbody = new MailVerificationCodeBody();
        emailbody.setVerificationCode("VerificationCode");
        emailbody.recipientType("PG");

        // Act & Assert
        Mono<String> result = Assertions.assertDoesNotThrow(() ->
                templateService.executeTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, LANGUAGE, Mono.just(emailbody)));

        StepVerifier.create(result)
                .assertNext(r -> Assertions.assertTrue(r.contains("VerificationCode"),
                            "Result should include the verification code"))
                .verifyComplete();
    }

    @Test
    void executePdfTemplate_Success() {
        // Arrange
        var model = new MalfunctionLegalFact()
                .startDate("TEST_startDate")
                .timeReferenceStartDate("TEST_timeReferenceStartDate")
                .endDate("TEST_endDate")
                .timeReferenceEndDate("TEST_timeReferenceEndDate")
                .htmlDescription("<p>Sample <b>TEST_description</b></p>");

        // Act & Assert
        Mono<byte[]> result = Assertions.assertDoesNotThrow(() ->
                templateService.executePdfTemplate(TemplatesEnum.MALFUNCTION_LEGAL_FACT, LANGUAGE, Mono.just(model)));

        StepVerifier.create(result)
                .assertNext(r -> {
                    Assertions.assertNotNull(r);
                    Assertions.assertTrue(r.length > 0, "PDF byte array should not be empty");

                    // Verify it's a PDF by checking the first bytes for the "%PDF-" signature
                    String pdfHeader = new String(r, 0, 5); // Read the first 5 bytes
                    Assertions.assertEquals("%PDF-", pdfHeader, "Generated file is not a valid PDF");
                })
                .verifyComplete();
    }

    @Test
    void executeTextTemplateAsString_Success() {
        // Act & Assert
        StepVerifier.create(templateService.executeTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_SUBJECT, LANGUAGE))
                .expectNext("SEND - Conferma la tua e-mail")
                .verifyComplete();
    }

    @Test
    void executePdfTemplate_ShouldFailWhenDocumentCompositionThrowsException() {
        // Arrange
        documentComposition = Mockito.mock(DocumentComposition.class);
        templateService = new TemplateService(documentComposition, templateConfig);
        MailVerificationCodeBody emailbody = new MailVerificationCodeBody();
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
        MailVerificationCodeBody emailbody = new MailVerificationCodeBody();
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