package it.pagopa.pn.templatesengine.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class JacksonUtils {
    private JacksonUtils(){}
    public static String ObjectToYamlString(Object obj) throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
        String yamlString = yamlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        sb.append(yamlString);
        return sb.toString();
    }
}
