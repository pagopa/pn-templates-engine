package it.pagopa.pn.templatesengine.resolver.impl;

import it.pagopa.pn.templatesengine.resolver.Resolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;

/**
 * Resolver che converte il contenuto di una risorsa (come un'immagine) in una stringa Base64.
 * Utilizza un resolver URL per ottenere scaricare dati binari e quindi li codifica in Base64.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Base64Resolver implements Resolver<byte[], String>{
    private final Tika tika = new Tika();

    /**
     * Risolve l'URL fornito e restituisce i dati codificati in Base64.
     *
     * @param value URL della risorsa da risolvere e convertire in Base64.
     * @return Mono che emette una stringa con i dati codificati in Base64.
     */
    public Mono<String> resolve(byte[] value) {
        // Phase 1: Resolve URL and get binary data
        return Mono.just(encodeToBase64(value));  // Phase 2: Encode data into Base64
    }

    /**
     * Converte i dati binari in una stringa Base64.
     *
     * @param data I dati binari da codificare.
     * @return La stringa contenente i dati codificati in Base64, nel formato Data URL.
     */
    private String encodeToBase64(byte[] data) {
        // Detect MIME type automatically from image content
        String mimeType = detectMimeType(data);

        // Convert binary data to Base64
        String base64Data = Base64.getEncoder().encodeToString(data);

        // Build the Data URL string
        return "data:" + mimeType + ";base64," + base64Data;
    }

    /**
     * Rileva il tipo MIME del contenuto binario utilizzando.
     *
     * @param data I dati binari di cui si vuole rilevare il tipo MIME.
     * @return Il tipo MIME rilevato, o "application/octet-stream" in caso di errore.
     */
    private String detectMimeType(byte[] data) {
        try {
            // Use Tika to detect MIME type from binary content
            return tika.detect(data);
        } catch (Exception e) {
            log.error("Error detecting MIME type", e);
            return "application/octet-stream"; // Fallback type if unable to detect
        }
    }
}
