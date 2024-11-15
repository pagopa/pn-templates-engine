package it.pagopa.pn.templatesengine.utils;

import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TemplateUtilsTest {

    private static final String VALID_PATH = "templates-assets/email_test.html";
    private static final String INVALID_PATH = "invalid/path/template.ftl";
    private static final String TEMPLATE_CONTENT = "<div>test</div>";

    @Test
    void getFormattedPath_ShouldReturnFormattedUriPath_WhenValidRelativePathIsProvided() {
        // Arrange
        File mockFile = mock(File.class);
        when(mockFile.getAbsolutePath()).thenReturn("/absolute/path/to/template_it.ftl");

        // Act & Assert
        String formattedPath = assertDoesNotThrow(() -> TemplateUtils.getFormattedPath(VALID_PATH));
        assertTrue(formattedPath.contains(VALID_PATH));
    }

    @Test
    void getFormattedPath_ShouldThrowPnGenericException_WhenInvalidPathIsProvided() {
        PnGenericException exception = assertThrows(PnGenericException.class,
                () -> TemplateUtils.getFormattedPath(INVALID_PATH));
        assertEquals(ExceptionTypeEnum.ERROR_FILE_READING, exception.getExceptionType());
    }

    @Test
    void loadTemplateContent_ShouldReturnTemplateContent_WhenValidTemplatePathIsProvided() {
        String content = assertDoesNotThrow(() -> TemplateUtils.loadTemplateContent(VALID_PATH));
        assertTrue(content.contains(TEMPLATE_CONTENT));
    }

    @Test
    void loadTemplateContent_ShouldThrowPnGenericException_WhenInvalidPathIsProvided() {
        PnGenericException exception = assertThrows(PnGenericException.class,
                () -> TemplateUtils.loadTemplateContent(INVALID_PATH));
        assertEquals(ExceptionTypeEnum.ERROR_TEMPLATE_LOADING, exception.getExceptionType());
    }

}