package it.pagopa.pn.templatesengine.resolver.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrlResolverTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private PnTemplatesEngineConfig config;

    private UrlResolver urlResolver;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        urlResolver = new UrlResolver(webClientBuilder, config);
    }

    @Test
    void whenResolveWithValidUrl_thenReturnContent() {
        // Arrange
        when(config.getUrlResolverTimeout()).thenReturn(Duration.ofSeconds(10));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        byte[] expectedContent = "test content".getBytes();
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.just(expectedContent));

        // Act
        Mono<byte[]> result = urlResolver.resolve("http://valid.url");

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedContent)
                .verifyComplete();
    }

    @Test
    void whenResolveWithNullUrl_thenReturnNull() {
        // Arrange Act
        Mono<byte[]> result = urlResolver.resolve(null);

        // Assert
        assertNull(result);
    }

    @Test
    void whenResolveWithBlankUrl_thenThrowIllegalArgumentException() {
        // Arrange Act
        Mono<byte[]> result = urlResolver.resolve("   ");

        // Assert
        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void whenWebClientReturnsError_thenPropagateError() {
        // Arrange
        when(config.getUrlResolverTimeout()).thenReturn(Duration.ofSeconds(10));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.error(new RuntimeException("Network error")));

        // Act
        Mono<byte[]> result = urlResolver.resolve("http://error.url");

        // Assert
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void whenResolveWithSlowResponse_thenTimeoutOccurs() {
        // Arrange
        Duration timeout = Duration.ofSeconds(1);
        when(config.getUrlResolverTimeout()).thenReturn(timeout);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        // Simula una risposta lenta (3s)
        when(responseSpec.bodyToMono(byte[].class))
                .thenReturn(Mono.delay(Duration.ofSeconds(3)).then(Mono.just("late content".getBytes())));

        // Act & Assert
        StepVerifier.create(urlResolver.resolve("http://slow.url"))
                .expectError(TimeoutException.class)
                .verify();
    }
}