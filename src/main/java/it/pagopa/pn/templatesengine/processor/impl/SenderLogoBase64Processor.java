package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
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
 * Processore che risolve il logo dell'ente mittente come stringa Base64.
 *
 * <p>A partire dal {@code paId}, costruisce l'URL del logo utilizzando il template
 * configurato in {@link PnTemplatesEngineConfig#getSenderLogoUrlTemplate()}, quindi
 * lo risolve in Base64 tramite il {@link TemplateValueResolver}.</p>
 *
 * <p>Qualsiasi model di output che implementi {@link OutputModel} può essere
 * popolato da questo processore, indipendentemente dalla classe concreta.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SenderLogoBase64Processor
        implements TemplateModelProcessor<String, SenderLogoBase64Processor.OutputModel> {

    private final TemplateValueResolver templateValueResolver;
    private final SenderLogoUrlProcessor senderLogoUrlProcessor;

    /**
     * Contratto di output per i model che supportano il logo del mittente in Base64.
     * Implementato dai Generated Params che espongono {@code senderLogoBase64} nel template FreeMarker.
     */
    public interface OutputModel {
        void setSenderLogoBase64(String logoBase64);
    }

    /**
     * Risolve il logo del mittente in Base64 e lo imposta nell'output.
     *
     * <p>Se il {@code paId} è nullo/vuoto o l'URL template non è configurato,
     * imposta {@code null}. In caso di errore durante la risoluzione,
     * logga un warning e imposta {@code null} senza propagare l'eccezione.</p>
     *
     * @param template  il template in fase di elaborazione (usato dal resolver per applicare la configurazione)
     * @param paId      l'identificativo della PA mittente
     * @param outParams l'oggetto output in cui impostare il logo
     * @return un Mono che completa quando l'elaborazione è terminata
     */
    @Override
    public Mono<Void> process(TemplatesEnum template, String paId, OutputModel outParams) {
        String url = senderLogoUrlProcessor.buildSenderLogoUrl(paId);

        if (url == null) {
            outParams.setSenderLogoBase64(null);
            return Mono.empty();
        }

        return templateValueResolver
                .resolve(TO_BASE64_RESOLVER + DIVIDER + url,
                        template,
                        TemplatesParamsEnum.SENDER_LOGO)
                .doOnNext(outParams::setSenderLogoBase64)
                .onErrorResume(e -> {
                    log.warn("Unable to resolve sender logo as Base64 for paId={}", paId, e);
                    outParams.setSenderLogoBase64(null);
                    return Mono.empty();
                })
                .then();
    }
}