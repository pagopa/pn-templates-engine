package it.pagopa.pn.templatesengine.resolver.impl;

import it.pagopa.pn.templatesengine.resolver.Resolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class ToBase64Resolver implements Resolver<String>{
    private final UrlResolver urlResolver;
    private final Tika tika = new Tika();

    public Mono<String> resolve(String paramValue) {
        // Phase 1: Resolve URL and get binary data
        return urlResolver.resolve(paramValue)
                .map(this::encodeToBase64);  // Phase 2: Encode data into Base64
    }

    private String encodeToBase64(byte[] data) {
        // Detect MIME type automatically from image content
        String mimeType = detectMimeType(data);

        // Convert binary data to Base64
        String base64Data = Base64.getEncoder().encodeToString(data);

        // Build the Data URL string
        return "data:" + mimeType + ";base64," + base64Data;
    }

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
