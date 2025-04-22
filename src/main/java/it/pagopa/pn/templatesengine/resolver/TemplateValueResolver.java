package it.pagopa.pn.templatesengine.resolver;

import it.pagopa.pn.templatesengine.config.ResolverWhitelistConfig;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.resolver.impl.ToBase64Resolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static it.pagopa.pn.templatesengine.resolver.ResolverEnum.TO_BASE64_RESOLVER;

/**
 * Classe responsabile della risoluzione dei valori dei parametri di un template.
 * Utilizza diversi resolver per elaborare i valori dei parametri in base alla configurazione specificata.
 */
@Slf4j
@Component
public class TemplateValueResolver {
    private static final String DIVIDER = ":";

    private final TemplateConfig templateConfig;
    private final ResolverWhitelistConfig whitelistConfig;
    Map<ResolverEnum, Resolver<String>> resolverMap;

    public TemplateValueResolver(ToBase64Resolver toBase64Resolver,
                                 TemplateConfig templateConfig,
                                 ResolverWhitelistConfig whitelistConfig) {
        this.templateConfig = templateConfig;
        this.whitelistConfig = whitelistConfig;
        resolverMap =  Map.of(TO_BASE64_RESOLVER, toBase64Resolver);
    }

    /**
     * Risolve il valore del parametro del template utilizzando il resolver configurato
     * nel parametro.
     * <p>
     * Es. param = "TO_BASE64_RESOLVER:http://example.com"
     *
     * @param paramValue Il valore del parametro da risolvere.
     * @param template Il template di riferimento.
     * @param param Il parametro del template da risolvere.
     * @return Mono che emette il valore risolto o vuoto se non è possibile risolvere.
     */
    public Mono<String> resolve(String paramValue,
                                TemplatesEnum template,
                                TemplatesParamsEnum param) {
        log.info("Resolver call - template={}.{}, paramValue={}", template, param, paramValue );

        var resolverConf = getResolverConfig(template, param);
        log.info("Resolver config={}", resolverConf );

        if(paramValue == null)
            return Mono.empty();

        // Configurazione resolver non specificata o non abilitato
        if(resolverConf == null || !resolverConf.isEnabled())
            return Mono.just(paramValue);

        if(resolverConf.isBypassAllWithNull())
            return Mono.empty();

        // Estraggo il prefisso
        int dividerIndex = paramValue.indexOf(DIVIDER);

        // Nessun prefisso -> nessun resolver
        if (dividerIndex == -1)
            return Mono.just(paramValue);

        // Ottengo il resolver corrispondente
        String resolverKey = paramValue.substring(0, dividerIndex);
        String cleanedValue = paramValue.substring(dividerIndex + 1);

        // Controllo se è presente nella whitelist
        if(resolverConf.isWhitelistEnabled()
                && !whitelistConfig.isInWhitelist(template, param, cleanedValue))
            return Mono.empty();

        var resolverEnum = ResolverEnum.fromValue(resolverKey);
        if(resolverEnum == null)
            return Mono.just(paramValue);

        Resolver<String> resolver = resolverMap.get(resolverEnum);

        return resolver != null
                ? resolver.resolve(cleanedValue)
                    .onErrorResume(e -> {
                        log.error("Error during resolution of value: {}", cleanedValue, e);
                        return resolverConf.isReturnNullOnError() ? Mono.empty() : Mono.just(paramValue);
                    })
                    .doOnNext((v)-> log.info("Resolved value={}", StringUtils.abbreviate(v, 50)))
                : Mono.just(paramValue);
    }

    /**
     * Recupera la configurazione del resolver per il template e il parametro specificato.
     *
     * @param template Il template di riferimento.
     * @param param Il parametro del template per cui recuperare la configurazione.
     * @return La configurazione del resolver per il parametro, o null se non esiste.
     */
    private TemplateConfig.Resolver getResolverConfig(TemplatesEnum template, TemplatesParamsEnum param) {
        // Non può essere null perché il controllo viene fatto da TemplatesConfig
        var resolversMap = templateConfig.getTemplates().get(template).getResolvers();
        if(resolversMap == null)
            return null;

        return resolversMap.get(param);
    }
}
