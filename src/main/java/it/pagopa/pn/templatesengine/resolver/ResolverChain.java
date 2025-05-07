package it.pagopa.pn.templatesengine.resolver;

import reactor.core.publisher.Mono;

import java.util.List;

public class ResolverChain<I, O> {
    private final List<Resolver<?, ?>> resolvers;

    public ResolverChain(List<Resolver<?, ?>> resolvers) {
        this.resolvers = resolvers;
    }

    @SuppressWarnings("unchecked")
    public Mono<O> execute(I input) {
        Mono<?> result = Mono.just(input);
        for (Resolver<?, ?> resolver : resolvers) {
            Resolver<Object, Object> typedResolver = (Resolver<Object, Object>) resolver;
            result = result.flatMap(typedResolver::resolve);
        }
        return (Mono<O>) result;
    }
}