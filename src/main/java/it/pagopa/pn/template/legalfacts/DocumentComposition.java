package it.pagopa.pn.template.legalfacts;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;


public interface DocumentComposition {
    Mono<String> executeTextTemplate(String content, Map<String, Object> mapTemplateModel);
    Flux<Byte> executePdfTemplate(String content, Map<String, Object> mapTemplateModel);
}