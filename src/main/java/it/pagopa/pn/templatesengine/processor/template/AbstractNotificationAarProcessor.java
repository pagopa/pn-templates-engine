package it.pagopa.pn.templatesengine.processor.template;

import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.processor.SimpleProcessor;
import it.pagopa.pn.templatesengine.resolver.impl.Base64Resolver;
import it.pagopa.pn.templatesengine.resolver.impl.UrlResolver;
import lombok.Builder;
import reactor.core.publisher.Mono;

import java.util.Map;

public abstract class AbstractNotificationAarProcessor<T> extends SimpleProcessor<T> {
    private final Base64Resolver base64Resolver;
    private final UrlResolver urlResolver;

    public AbstractNotificationAarProcessor(
            TemplateConfig templateConfig,
            Base64Resolver base64Resolver,
            UrlResolver urlResolver) {
        super(templateConfig);
        this.base64Resolver = base64Resolver;
        this.urlResolver = urlResolver;
    }

    protected Mono<String> resolveBase64Logo(String url) {
        return urlResolver.resolve(url)
                .flatMap(base64Resolver::resolve);
    }

    @Builder
    public static class GeneratedParams {
        public String senderLogoBase64;
    }
}
