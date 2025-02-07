package it.pagopa.pn.templatesengine.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(QrCodeApiController.class)
public class QrCodeApiControllerTest {
    public static final String API_URI = "/templates-engine-private/v1/qrcode-generator";
    @Autowired
    WebTestClient webTestClient;
    @Test
    void qrCodeTestWebClient() {
        String urlExample = "test";
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(API_URI)
                        .queryParam("url", urlExample)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.base64value").exists()
                .jsonPath("$.base64value").isNotEmpty();
    }

}
