package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class CharNormalizerProcessor
        implements TemplateModelProcessor<InformalCommunication, CharNormalizerProcessor.OutputModel> {

    /**
     * * Interfaccia per l'output del processore che definisce i metodi per impostare i campi normalizzati.
     * */
    public interface OutputModel {
        void setPrimaryContentHtml(String html);
        void setSecondaryContentHtml(String html);
        String getPrimaryContentHtml();
        String getSecondaryContentHtml();
    }

    /**
     * * Mapping caratteri speciali
     * */
    private static final Map<String, String> CHAR_MAP = Map.ofEntries(
            Map.entry("č", "&#269;"),
            Map.entry("Č", "&#268;"),
            Map.entry("š", "&#353;"),
            Map.entry("ž", "&#382;"),
            Map.entry("'", "&#39;"),
            Map.entry("é", "&#xE9;"),
            Map.entry("è", "&egrave;"),
            Map.entry("ê", "&ecirc;"),
            Map.entry("È", "&Egrave;"),
            Map.entry("ç", "&#xE7;"),
            Map.entry("à", "&#xE0;"),
            Map.entry("À", "&#xC0;"),
            Map.entry("û", "&#xFB;"),
            Map.entry("ô", "&#xF4;"),
            Map.entry("ü", "&#252;"),
            Map.entry("ö", "&#246;"),
            Map.entry("Ö", "&#214;"),
            Map.entry("ä", "&#228;"),
            Map.entry("*", "&#42;")
    );

    static Map<String, String> getCharMap() {
        return CHAR_MAP;
    }

    /**
     * Converte i campi del modello di input in campi normalizzati, sostituendo i caratteri speciali definiti nella mappa CHAR_MAP.
     *
     * @param template  il template in fase di elaborazione
     * @param model      il modello di input contenente i dati da elaborare
     * @param outParams l'oggetto output che riceverà i campi normalizzati
     * @return un Mono che completa immediatamente dopo aver popolato l'output
     */
    @Override
    public Mono<Void> process(TemplatesEnum template, InformalCommunication model, OutputModel outParams) {
        if (model == null) {
            return Mono.empty();
        }

        model.setSubject(normalize(model.getSubject()));

        if (outParams.getPrimaryContentHtml() != null) {
            outParams.setPrimaryContentHtml(normalize(outParams.getPrimaryContentHtml()));
        }

        if (outParams.getSecondaryContentHtml() != null) {
            outParams.setSecondaryContentHtml(normalize(outParams.getSecondaryContentHtml()));
        }

        if (model.getSender() != null) {
            model.getSender().setDenomination(normalize(model.getSender().getDenomination()));
            model.getSender().setService(normalize(model.getSender().getService()));
        }

        if (model.getRecipient() != null) {
            model.getRecipient().setDenomination(normalize(model.getRecipient().getDenomination()));
        }

        return Mono.empty();
    }

    private String normalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String result = input;
        for (Map.Entry<String, String> entry : CHAR_MAP.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
