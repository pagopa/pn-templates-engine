package it.pagopa.pn.templates.engine.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class TemplateUtils {

  public static Map<String, Object> dtoToMap(Object object) {
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<HashMap<String,Object>> typeRef
            = new TypeReference<>() {};
    return mapper.convertValue(object, typeRef);
  }
}