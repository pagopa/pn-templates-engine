package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.templatesengine.utils.parameters.CachedParameterStoreConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Component
public class ResolverWhitelistConfig {
    private final TemplateConfig templateConfig;
    private final CachedParameterStoreConsumer parameterStoreConsumer;

    private final Map<Tuple2<TemplatesEnum, TemplatesParamsEnum>, Set<String>> whitelistParametersStoresMap = new HashMap<>();
    private final Map<Tuple2<TemplatesEnum, TemplatesParamsEnum>, Set<String>> whitelist = new HashMap<>();

    @PostConstruct
    public void initializeWhitelist() {
        initializeWhitelistParametersStoresMap();
        whitelistParametersStoresMap.forEach((key, parameters) -> {
            parameters.forEach(p->{
                Optional<String[]> result = parameterStoreConsumer.getParameter(p, String[].class);
                result.ifPresent(strings -> whitelist.put(key, Set.of(strings)));
            });
        });
        log.info("Whitelist Loaded: {}", whitelist);
    }

    public boolean isInWhitelist(TemplatesEnum template, TemplatesParamsEnum param, String value) {
        var values = whitelist.getOrDefault(Tuples.of(template, param), Set.of());
        return values.contains(value);
    }

    private void initializeWhitelistParametersStoresMap() {
        templateConfig.getTemplates().forEach((template, entry) -> {
            var resolvers = entry.getResolvers();
            if (resolvers != null) {
                resolvers.forEach((templateParam, resolverConfig) -> {
                    if (resolverConfig.getWhitelistParameterStores() != null && resolverConfig.isWhitelistEnabled()) {
                        whitelistParametersStoresMap.put(
                                Tuples.of(template, templateParam),
                                resolverConfig.getWhitelistParameterStores()
                                        .stream()
                                        .collect(Collectors.toUnmodifiableSet())
                        );
                    }
                });
            }
        });
        log.info("Whitelist Parameters Stores Map Loaded: {}", whitelistParametersStoresMap);
    }
}
