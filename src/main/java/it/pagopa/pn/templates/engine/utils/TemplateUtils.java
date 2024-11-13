package it.pagopa.pn.templates.engine.utils;

import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

import static it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum.ERROR_FILE_READING;


@Slf4j
@Component
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

}