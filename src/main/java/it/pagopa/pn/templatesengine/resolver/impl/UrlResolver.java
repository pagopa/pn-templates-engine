package it.pagopa.pn.templatesengine.resolver.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import it.pagopa.pn.templatesengine.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Resolver che risolve un URL e recupera i dati binari associati a tale URL.
 */
@Slf4j
@Component
public class UrlResolver implements Resolver<byte[]> {
    private final WebClient webClient;
    private final PnTemplatesEngineConfig config;

    public UrlResolver(WebClient.Builder webClientBuilder, PnTemplatesEngineConfig config) {
        this.webClient = webClientBuilder.build();
        this.config = config;
    }

    /**
     * Risolve l'URL fornito e restituisce i dati binari associati.
     *
     * @param paramValue URL della risorsa da risolvere.
     * @return Mono che emette i dati binari come byte array, oppure un errore se l'URL è nullo o vuoto.
     */
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
                .bodyToMono(byte[].class)
                .timeout(config.getUrlResolverTimeout());
    }
}
