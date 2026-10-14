package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import it.pagopa.pn.templatesengine.config.ResolverWhitelistConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import it.pagopa.pn.templatesengine.resolver.TemplateValueResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static it.pagopa.pn.templatesengine.resolver.ResolverEnum.TO_BASE64_RESOLVER;
import static it.pagopa.pn.templatesengine.resolver.TemplateValueResolver.DIVIDER;

/**
 * Processore che risolve il logo dell'ente mittente come stringa url dell'immagine.
 *
 * <p>A partire dal {@code paId}, costruisce l'URL del logo utilizzando il template
 * configurato in {@link PnTemplatesEngineConfig#getSenderLogoUrlTemplate()}.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SenderLogoUrlProcessor
        implements TemplateModelProcessor<String, SenderLogoUrlProcessor.OutputModel> {

    private final PnTemplatesEngineConfig pnTemplatesEngineConfig;
    private final ResolverWhitelistConfig resolverWhitelistConfig;

    /**
     * Contratto di output per i model che supportano il logo del mittente come URL.
     * Implementato dai Generated Params che espongono {@code senderLogoUrl} nel template FreeMarker.
     */
    public interface OutputModel {
        void setSenderLogoUrl(String logoUrl);
    }

    /**
     * Risolve il logo del mittente come URL e lo imposta nell'output.
     *
     * <p>Se il {@code paId} è nullo/vuoto o l'URL template non è configurato,
     * imposta {@code null}. In caso di errore durante la risoluzione,
     * logga un debug e imposta {@code null} senza propagare l'eccezione.</p>
     *
     * @param template  il template in fase di elaborazione (usato dal resolver per applicare la configurazione)
     * @param paId      l'identificativo della PA mittente
     * @param outParams l'oggetto output in cui impostare il logo
     * @return un Mono che completa quando l'elaborazione è terminata
     */
    @Override
    public Mono<Void> process(TemplatesEnum template, String paId, OutputModel outParams) {
        String url = buildSenderLogoUrl(paId);
        if (url != null && resolverWhitelistConfig.isWhitelistEnabled(template, TemplatesParamsEnum.SENDER_LOGO)
                && !resolverWhitelistConfig.isInWhitelist(template, TemplatesParamsEnum.SENDER_LOGO, url)) {
            log.debug("URL not allowed by resolver whitelist: {}", url);
            url = null;
        }
        outParams.setSenderLogoUrl(url);
        return Mono.empty();
    }

    /**
     * Costruisce l'URL del logo sostituendo il placeholder {@code <PA_ID>} nel template configurato.
     *
     * @param paId l'identificativo della PA da inserire nell'URL
     * @return l'URL completo, oppure {@code null} se {@code paId} o il template URL sono assenti
     */
    public String buildSenderLogoUrl(String paId) {
        if (paId == null || paId.isBlank()) {
            return null;
        }

        String urlTemplate = pnTemplatesEngineConfig.getSenderLogoUrlTemplate();

        if (urlTemplate == null || urlTemplate.isBlank()) {
            return null;
        }

        return urlTemplate.replace("<PA_ID>", paId);
    }
}