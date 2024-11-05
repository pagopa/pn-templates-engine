package it.pagopa.pn.templates.engine.dao;

import static it.pagopa.pn.templates.engine.exceptions.TemplateExceptionCodes.ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class FileDao {

  public String getFile(String file) {
    try {
      log.info("Read fileAndPath={} ", file);
      Path path = Paths.get(file);
      return Files.readString(path);
    } catch (IOException e) {
      throw new PnInternalException("File not found",
          ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED, e);
    }
  }

}
