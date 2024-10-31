package it.pagopa.pn.template.dao;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.stereotype.Component;

@Component
public class FileDao {

  public String getFile(String file) {
    try {
      Path path = Paths.get(file);
      return Files.readString(path);
    } catch (IOException e) {
      throw new PnInternalException("File non trovato" + file, "404");
    }
  }

}
