package it.pagopa.pn.templatesengine.utils.parameteres;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import it.pagopa.pn.templatesengine.utils.parameters.CachedParameterStoreConsumer;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CachedParameterStoreConsumerTest {

    @Mock
    private SsmClient mockSsmClient;

    @Mock
    private PnTemplatesEngineConfig mockConfig;

    private CachedParameterStoreConsumer cachedParameterStoreConsumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        cachedParameterStoreConsumer = new CachedParameterStoreConsumer(mockSsmClient, mockConfig);
        objectMapper = new ObjectMapper();
    }

    @Test
    void getParameter_ShouldFetchFromSSM_WhenCacheIsEmpty() throws JsonProcessingException {
        // Arrange
        String parameterName = "test-config";
        TestConfig expectedConfig = new TestConfig();
        expectedConfig.setKey("testKey");
        expectedConfig.setValue(42);

        // Convert to JSON string
        String jsonConfig = objectMapper.writeValueAsString(expectedConfig);

        // Mock SSM client to return a valid response
        GetParameterResponse response = GetParameterResponse.builder()
                .parameter(Parameter.builder().value(jsonConfig).build())
                .build();
        when(mockSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        // Act
        Optional<TestConfig> result = cachedParameterStoreConsumer.getParameter(parameterName, TestConfig.class);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedConfig.getKey(), result.get().getKey());
        assertEquals(expectedConfig.getValue(), result.get().getValue());
        verify(mockSsmClient, times(1)).getParameter(any(GetParameterRequest.class));
    }

    @Test
    void getParameter_ShouldReturnCachedValue_WhenWithinTTL() throws JsonProcessingException {
        // Arrange
        String parameterName = "test-config";
        TestConfig expectedConfig = new TestConfig();
        expectedConfig.setKey("testKey");
        expectedConfig.setValue(42);

        // Convert to JSON string
        String jsonConfig = objectMapper.writeValueAsString(expectedConfig);

        // Mock SSM client to return a valid response
        GetParameterResponse response = GetParameterResponse.builder()
                .parameter(Parameter.builder().value(jsonConfig).build())
                .build();
        when(mockSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        // Mock cache TTL to be longer than default
        when(mockConfig.getParameterStoreCacheTTL()).thenReturn(Duration.ofHours(1));

        // First call to populate cache
        cachedParameterStoreConsumer.getParameter(parameterName, TestConfig.class);

        // Reset mock to verify no additional calls
        reset(mockSsmClient);

        // Act
        Optional<TestConfig> result = cachedParameterStoreConsumer.getParameter(parameterName, TestConfig.class);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedConfig.getKey(), result.get().getKey());
        assertEquals(expectedConfig.getValue(), result.get().getValue());
        verify(mockSsmClient, never()).getParameter(any(GetParameterRequest.class));
    }

    @Test
    void getParameter_ShouldRefetchFromSSM_WhenCacheExpired() throws JsonProcessingException {
        // Arrange
        String parameterName = "test-config";
        TestConfig expectedConfig = new TestConfig();
        expectedConfig.setKey("testKey");
        expectedConfig.setValue(42);

        // Convert to JSON string
        String jsonConfig = objectMapper.writeValueAsString(expectedConfig);

        // Mock SSM client to return a valid response
        GetParameterResponse response = GetParameterResponse.builder()
                .parameter(Parameter.builder().value(jsonConfig).build())
                .build();
        when(mockSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        // Mock very short TTL to force cache expiration
        when(mockConfig.getParameterStoreCacheTTL()).thenReturn(Duration.ofSeconds(1));

        // First call to populate cache
        cachedParameterStoreConsumer.getParameter(parameterName, TestConfig.class);

        // Wait to ensure cache expires
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        Optional<TestConfig> result = cachedParameterStoreConsumer.getParameter(parameterName, TestConfig.class);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedConfig.getKey(), result.get().getKey());
        assertEquals(expectedConfig.getValue(), result.get().getValue());
        verify(mockSsmClient, times(2)).getParameter(any(GetParameterRequest.class));
    }

    @Test
    void getParameter_ShouldRemoveCacheAndRefetch_WhenCachedTypeIncompatible() throws JsonProcessingException {
        // Arrange
        String parameterName = "test-config";
        TestConfig expectedConfig = new TestConfig();
        expectedConfig.setKey("testKey");
        expectedConfig.setValue(42);

        // Convert to JSON string
        String jsonConfig = objectMapper.writeValueAsString(expectedConfig);

        // Mock SSM client to return a valid response
        GetParameterResponse response = GetParameterResponse.builder()
                .parameter(Parameter.builder().value(jsonConfig).build())
                .build();
        when(mockSsmClient.getParameter(any(GetParameterRequest.class))).thenReturn(response);

        // Mock cache TTL to be longer than default
        when(mockConfig.getParameterStoreCacheTTL()).thenReturn(Duration.ofHours(1));

        // First call to populate cache with TestConfig
        cachedParameterStoreConsumer.getParameter(parameterName, TestConfig.class);

        // Attempt to retrieve with incompatible type
        Optional<TestConfig2> result = cachedParameterStoreConsumer.getParameter(parameterName, TestConfig2.class);

        // Assert
        assertTrue(result.isPresent());
        verify(mockSsmClient, times(2)).getParameter(any(GetParameterRequest.class));
    }

    // Test DTO for deserialization
    @Setter
    @Getter
    private static class TestConfig {
        // Getters and setters for Jackson
        private String key;
        private int value;
    }

    @Setter
    @Getter
    private static class TestConfig2 {
        // Getters and setters for Jackson
        private String key;
        private int value;
    }
}