package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import it.pagopa.pn.templatesengine.resolver.TemplateValueResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformalSenderLogoProcessor
        implements TemplateModelProcessor<String, InformalSenderLogoProcessor.OutputModel> {

    private final TemplateValueResolver templateValueResolver;
    private final PnTemplatesEngineConfig pnTemplatesEngineConfig;


    /**
     * Contratto per i model di output che supportano il logo del mittente.
     */
    public interface OutputModel {
        void setSenderLogoBase64(String logoBase64);
    }

    @Override
    public void process(String paId, OutputModel outParams) {
        String url = buildSenderLogoUrl(paId);

        if (url == null) {
            outParams.setSenderLogoBase64(null);
            return;
        }

        try {
            outParams.setSenderLogoBase64(
                            templateValueResolver
                                    .resolve("TO_BASE64_RESOLVER:" + url,
                                            TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION,
                                            TemplatesParamsEnum.SENDER_LOGO_BASE64)
                                    .block()
            );
        } catch (Exception e) {
            log.warn("Unable to resolve sender logo as Base64 for paId={}", paId, e);
            outParams.setSenderLogoBase64(null);
        }
    }

    /**
     * Builds the URL for the sender logo by replacing the placeholder in the template with the given PA ID.
     *
     * @param paId the PA ID to be inserted into the URL template
     * @return the formatted URL containing the specified PA ID
     */
    private String buildSenderLogoUrl(String paId) {
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