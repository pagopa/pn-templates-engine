package it.pagopa.pn.template.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TemplateUtils {

  static ObjectMapper objectMapper = new ObjectMapper();

  public static Map<String, Object> dtoToMap(Object object) {
    return objectMapper.convertValue(object, new TypeReference<Map<String, Object>>() {
    });
  }

}
