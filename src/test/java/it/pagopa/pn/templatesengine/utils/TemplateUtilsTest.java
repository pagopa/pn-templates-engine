package it.pagopa.pn.templatesengine.utils;

import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplateUtilsTest {

    private static final String TEMPLATES_PATH = "templates-assets";
    private static final String FILE = "email_test.html";
    private static final String TEMPLATE_CONTENT = "<div>Hello ATTRIBUTE, ";

    @Test
    void getBaseURI_ShouldReturnFormattedUriPath_WhenValidRelativePathIsProvided() {
        // Act & Assert
        String baseUri = assertDoesNotThrow(() -> TemplateUtils.getBaseURI(TEMPLATES_PATH, FILE));
        assertNotNull(baseUri, "Base URI should not be null");
        assertTrue(baseUri.contains(TEMPLATES_PATH), "Base URI should include the template path");
        assertTrue(baseUri.endsWith("/"), "Base URI should end with a slash");
    }

    @Test
    void getBaseURI_ShouldThrowPnGenericException_WhenInvalidPathIsProvided() {
        // Act & Assert
        PnGenericException exception = assertThrows(
                PnGenericException.class,
                () -> TemplateUtils.getBaseURI(TEMPLATES_PATH, "nonExistingFile.html")
        );
    }

    @Test
    void loadTemplateContent_ShouldReturnTemplateContent_WhenValidTemplatePathIsProvided() {
        // Arrange
        String templatePath = TEMPLATES_PATH + "/" + FILE;
        // Act & Assert
        String content = assertDoesNotThrow(() -> TemplateUtils.loadTemplateContent(templatePath));
        assertTrue(content.contains(TEMPLATE_CONTENT));
    }

    @Test
    void loadTemplateContent_ShouldThrowPnGenericException_WhenInvalidPathIsProvided() {
        // Act & Assert
        PnGenericException exception = assertThrows(PnGenericException.class,
                () -> TemplateUtils.loadTemplateContent("invalid/path"));
        assertEquals(ExceptionTypeEnum.ERROR_TEMPLATE_LOADING, exception.getExceptionType());
    }

}