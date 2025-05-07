package it.pagopa.pn.templatesengine.processor;

import lombok.Getter;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface TemplateProcessor<T> {
    Mono<Map<ParamType, Object>> process(T requestParams);

    @Getter
    enum ParamType {
        ENV("env"),
        REQUEST("request"),
        GENERATED("generated");

        private final String value;

        ParamType(String value) {
            this.value = value;
        }
    }
}
