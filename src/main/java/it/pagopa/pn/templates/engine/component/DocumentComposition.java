package it.pagopa.pn.templates.engine.component;

import reactor.core.publisher.Mono;

import java.util.Map;


public interface DocumentComposition {
    Mono<String> executeTextTemplate(String templateFileName, Map<String, Object> mapTemplateModel);
    Mono<byte[]> executePdfTemplate(String templateFileName, Map<String, Object> mapTemplateModel);
}