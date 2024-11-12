package it.pagopa.pn.templates.engine.utils;

import static it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum.ERROR_FILE_READING;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.nio.file.Paths;
import java.util.Map;


@Slf4j
@Component
public class TemplateUtils {
    public static final String FIELD_LOGO = "logoBase64";

    private TemplateUtils() {}


    public static String getFormattedPath(String relativePath) {
        try {
            String absolutePath = new ClassPathResource(relativePath).getFile().getAbsolutePath();
            String uriPath = Paths.get(absolutePath).toUri().toString();
            return uriPath.replaceFirst("file:///", "file:");
        } catch (Exception exception) {
            throw new PnGenericException(ERROR_FILE_READING, exception.getMessage());
        }
    }

    public static Map<String, Object> appendLogoToMap(Map<String, Object> mappedTemplate, String value) {
        mappedTemplate.put(FIELD_LOGO, value);
        return mappedTemplate;
    }
}