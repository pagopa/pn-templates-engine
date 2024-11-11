package it.pagopa.pn.templates.engine.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.util.Map;


@Slf4j
public class TemplateMapper {
    private TemplateMapper() {}


    public static <T> Mono<Map<String, Object>> getMapByDto(Mono<T> dtoMono) {
        ObjectMapper objectMapper = new ObjectMapper();
        return dtoMono
                .doOnNext(dto -> log.info("Received model DTO: {}", dto))
                .map(dto -> objectMapper.convertValue(dto, new TypeReference<Map<String, Object>>() {}))
                .onErrorResume(exception -> {
                    log.error("Error converting DTO to Map: {}", exception.getMessage(), exception);
                    return Mono.error(new PnGenericException(ExceptionTypeEnum.ERROR_OBJECT_MAPPING, exception.getMessage()));
                });
    }
}