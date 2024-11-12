package it.pagopa.pn.templates.engine.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Slf4j
public class TemplateMapper {


    private TemplateMapper() {
    }

    public static <T> Map<String, Object> getMapByDto(T dto) {
        return new ObjectMapper().convertValue(dto, new TypeReference<Map<String, Object>>() {
        });
    }
}