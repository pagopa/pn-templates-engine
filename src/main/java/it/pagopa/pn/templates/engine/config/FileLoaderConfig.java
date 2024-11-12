package it.pagopa.pn.templates.engine.config;

import static it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum.ERROR_FILE_READING;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import it.pagopa.pn.templates.engine.utils.TemplateUtils;
import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.util.Base64;


@Configuration
public class FileLoaderConfig {

    @Bean(name = "base64FileContent")
    public String base64FileContent(@Value("${templatesPath}") String templatesPath,
                                    @Value("${templatesImagesPath}") String templatesImagesPath,
                                    @Value("${templatesLogo}") String templatesLogo) {
        String sendLogoPath = templatesPath.concat("/").concat(templatesImagesPath).concat("/").concat(templatesLogo);
        try (InputStream ioStream = new ClassPathResource(sendLogoPath).getInputStream()) {
            byte[] fileBytes = IOUtils.toByteArray(ioStream);
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (Exception exception) {
            throw new PnGenericException(ERROR_FILE_READING, exception.getMessage());
        }
    }
}