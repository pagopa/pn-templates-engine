package it.pagopa.pn.templatesengine.resolver.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToBase64ResolverTest {

    @Mock
    private UrlResolver urlResolver;

    private ToBase64Resolver toBase64Resolver;

    @BeforeEach
    void setUp() {
        toBase64Resolver = new ToBase64Resolver(urlResolver);
    }

    @Test
    void testResolveToBase64() throws IOException {
        // Arrange
        String inputUrl = "http://example.com/image.jpg";
        InputStream is = getClass().getClassLoader().getResourceAsStream("img/logo.png");
        assertNotNull(is);
        byte[] pngData = is.readAllBytes();
        String expectedBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(pngData);

        when(urlResolver.resolve(inputUrl)).thenReturn(Mono.just(pngData));

        // Act
        Mono<String> result = toBase64Resolver.resolve(inputUrl);

        // Assert
        assertEquals(expectedBase64, result.block());
    }

    @Test
    void testDetectMimeType() throws IOException {
        // Arrange
        InputStream is = getClass().getClassLoader().getResourceAsStream("img/logo.png");
        assertNotNull(is);
        byte[] pngData = is.readAllBytes();

        // Act
        String detectedMime = ReflectionTestUtils.invokeMethod(toBase64Resolver, "detectMimeType", pngData);

        // Assert
        assertEquals("image/png", detectedMime);
    }

    @Test
    void testEncodeToBase64() throws IOException {
        // Arrange
        InputStream is = getClass().getClassLoader().getResourceAsStream("img/logo.png");
        assertNotNull(is);
        byte[] pngData = is.readAllBytes();

        // Act
        String base64String = ReflectionTestUtils.invokeMethod(toBase64Resolver, "encodeToBase64", pngData);

        // Assert
        assertNotNull(base64String);
        assertTrue(base64String.startsWith("data:image/png"));
        assertTrue(base64String.contains(";base64,"));
    }

    @Test
    void testResolveWithError() {
        // Arrange
        String inputUrl = "http://example.com/notfound.jpg";

        when(urlResolver.resolve(inputUrl)).thenReturn(Mono.error(new RuntimeException("Not Found")));

        // Act
        Mono<String> result = toBase64Resolver.resolve(inputUrl);

        // Assert
        assertThrows(RuntimeException.class, result::block);
    }
}
