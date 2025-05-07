package it.pagopa.pn.templatesengine.processor.template;

import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.NotificationAar;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.NotificationAarRaddAlt;
import it.pagopa.pn.templatesengine.resolver.impl.Base64Resolver;
import it.pagopa.pn.templatesengine.resolver.impl.UrlResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class NotificationAarRaddAltProcessor extends AbstractNotificationAarProcessor<NotificationAarRaddAlt> {
    public NotificationAarRaddAltProcessor(
            TemplateConfig templateConfig,
            Base64Resolver base64Resolver,
            UrlResolver urlResolver) {
        super(templateConfig, base64Resolver, urlResolver);
    }

    @Override
    public Mono<Map<ParamType, Object>> process(NotificationAarRaddAlt requestParams) {
        return super.resolveBase64Logo(requestParams.getSenderLogoBase64())
                .map(base64 ->
                        GeneratedParams.builder()
                                .senderLogoBase64(base64)
                                .build())
                .flatMap(generatedParams ->
                        super.process(requestParams, generatedParams));
    }
}
