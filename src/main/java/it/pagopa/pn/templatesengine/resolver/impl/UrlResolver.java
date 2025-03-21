package it.pagopa.pn.templatesengine.resolver.impl;

import it.pagopa.pn.templatesengine.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class UrlResolver implements Resolver<byte[]> {
    private final WebClient webClient;

    public UrlResolver(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<byte[]> resolve(String paramValue) {
        if (paramValue == null) {
            return null;
        }

        if (paramValue.isBlank()) {
            return Mono.error(new IllegalArgumentException("Blank url parameter"));
        }

        return webClient.get()
                .uri(paramValue)
                .retrieve()
                .bodyToMono(byte[].class);
    }
}
