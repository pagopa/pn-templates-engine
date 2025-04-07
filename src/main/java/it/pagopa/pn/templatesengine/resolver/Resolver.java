package it.pagopa.pn.templatesengine.resolver;

import reactor.core.publisher.Mono;

public interface Resolver<T> {
    Mono<T> resolve(String paramValue);
}
