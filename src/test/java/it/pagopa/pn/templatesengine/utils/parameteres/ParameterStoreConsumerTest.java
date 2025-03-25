package it.pagopa.pn.templatesengine.utils.parameteres;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.templatesengine.utils.parameters.ParameterStoreConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.Parameter;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParameterStoreConsumerTest {

    @Mock
    private SsmClient ssmClient;

    private ParameterStoreConsumer parameterStoreConsumer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        parameterStoreConsumer = new ParameterStoreConsumer(ssmClient);
    }

    @Test
    void getParameter_ShouldReturnValidObject_WhenJsonIsValid() throws Exception {
        String jsonValue = objectMapper.writeValueAsString(new TestData("testValue"));
        when(ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(
                GetParameterResponse.builder().parameter(Parameter.builder().value(jsonValue).build()).build()
        );

        Optional<TestData> result = parameterStoreConsumer.getParameter("testParameter", TestData.class);

        assertTrue(result.isPresent());
        assertEquals("testValue", result.get().value);
    }

    @Test
    void getParameter_ShouldReturnEmptyOptional_WhenParameterNotFound() {
        when(ssmClient.getParameter(any(GetParameterRequest.class))).thenThrow(SsmException.class);

        Optional<TestData> result = parameterStoreConsumer.getParameter("nonExistentParameter", TestData.class);

        assertFalse(result.isPresent());
    }

    @Test
    void getParameter_ShouldThrowException_WhenJsonIsInvalid() {
        when(ssmClient.getParameter(any(GetParameterRequest.class))).thenReturn(
                GetParameterResponse.builder().parameter(Parameter.builder().value("invalidJson").build()).build()
        );

        assertThrows(PnInternalException.class, () ->
                parameterStoreConsumer.getParameter("invalidJsonParameter", TestData.class)
        );
    }

    static class TestData {
        public String value;

        public TestData() {}

        public TestData(String value) {
            this.value = value;
        }
    }
}
