package it.pagopa.pn.template.dao;

import static it.pagopa.pn.template.exceptions.TemplateExceptionCodes.ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.template.config.TemplateConfig;
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

  private final TemplateConfig templateConfig;

  public String getFile(String file) {
    try {
      var fileAndPath = templateConfig.getPath() + file;
      log.info("Read fileAndPath={} ", fileAndPath);
      Path path = Paths.get(fileAndPath);
      return Files.readString(path);
    } catch (IOException e) {
      throw new PnInternalException("File not found",
          ERROR_TEMPLATES_CLIENT_DOCUMENTCOMPOSITIONFAILED, e);
    }
  }

}
