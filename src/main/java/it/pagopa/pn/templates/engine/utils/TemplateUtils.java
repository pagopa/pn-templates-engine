package it.pagopa.pn.templates.engine.utils;

import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum.ERROR_FILE_READING;

@Slf4j
public class TemplateUtils {

    private TemplateUtils() {
    }

    public static String getFormattedPath(String relativePath) {
        try {
            String absolutePath = new ClassPathResource(relativePath).getFile().getAbsolutePath();
            String uriPath = Paths.get(absolutePath).toUri().toString();
            return uriPath.replaceFirst("file:///", "file:/");
        } catch (Exception exception) {
            throw new PnGenericException(ERROR_FILE_READING, exception.getMessage());
        }
    }

    public static String loadTemplateContent(String templateFilePath) {
        try (InputStream inputStream = new ClassPathResource(templateFilePath).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATE_LOADING, e.getMessage());
        }
    }

}