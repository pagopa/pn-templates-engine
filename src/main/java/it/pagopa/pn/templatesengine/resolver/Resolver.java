package it.pagopa.pn.templatesengine.resolver;

import reactor.core.publisher.Mono;

public interface Resolver<I, O> {
    Mono<O> resolve(I paramValue);
}
