package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Processore che normalizza i caratteri speciali e non-ASCII presenti nei campi testuali
 * di una {@link InformalCommunication} e nei contenuti HTML dell'output, sostituendoli
 * con le corrispondenti entity HTML.
 */
@Component
public class CharNormalizerProcessor
        implements TemplateModelProcessor<InformalCommunication, CharNormalizerProcessor.OutputModel> {

    /**
     * Interfaccia per l'output del processore che definisce i metodi per impostare i campi normalizzati.
     */
    public interface OutputModel {
        void setPrimaryContentHtml(String html);
        void setSecondaryContentHtml(String html);
        String getPrimaryContentHtml();
        String getSecondaryContentHtml();
    }

    /**
     * Mapping caratteri speciali che non sono diacritici ma vanno comunque sostituiti con la relativa entity.
     * Tutti gli altri caratteri non-ASCII vengono convertiti automaticamente in entity esadecimali (&#xHH;)
     * tramite {@link #normalize(String)}, senza bisogno di elencarli manualmente.
     */
    private static final Map<String, String> CHAR_MAP = Map.ofEntries(
            Map.entry("'", "&#39;"),
            Map.entry("*", "&#42;")
    );

    /**
     * Restituisce la mappa dei caratteri speciali con la relativa entity HTML sostitutiva.
     *
     * @return la mappa immutabile dei caratteri e delle rispettive entity
     */
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


    /**
     * Normalizza la stringa in input sostituendo i caratteri presenti in {@link #CHAR_MAP}
     * con la relativa entity HTML e convertendo ogni altro carattere non-ASCII (code point
     * maggiore di 0x7F) nella sua entity esadecimale (&#x HH;).
     *
     * @param input la stringa da normalizzare
     * @return la stringa normalizzata, oppure l'input invariato se {@code null} o vuoto
     */
    private String normalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder(input.length());
        input.codePoints().forEach(codePoint -> {
            String character = new String(Character.toChars(codePoint));
            String mapped = CHAR_MAP.get(character);
            if (mapped != null) {
                result.append(mapped);
            } else if (codePoint > 0x7F) {
                result.append("&#x").append(Integer.toHexString(codePoint).toUpperCase()).append(";");
            } else {
                result.appendCodePoint(codePoint);
            }
        });
        return result.toString();
    }
}
