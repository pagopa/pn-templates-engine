package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplateConfig;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class SimpleProcessor<T> implements TemplateProcessor<T> {
    protected final TemplateConfig templateConfig;

    public SimpleProcessor(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
    }

    public Mono<Map<TemplateProcessor.ParamType, Object>> process(T requestParams) {
        return this.process(requestParams, new Object());
    }

    protected Mono<Map<TemplateProcessor.ParamType, Object>> process(T requestParams, Object generatedParams) {
        return Mono.just(Map.of(
                ParamType.ENV, templateConfig.getTemplatesStaticParams(),
                ParamType.REQUEST, requestParams,
                ParamType.GENERATED, generatedParams));
    }
}
