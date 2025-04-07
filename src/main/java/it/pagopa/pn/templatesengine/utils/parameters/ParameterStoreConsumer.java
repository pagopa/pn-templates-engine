package it.pagopa.pn.templatesengine.utils.parameters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;

import java.util.Optional;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;

@Slf4j
@RequiredArgsConstructor
public class ParameterStoreConsumer {
    private final SsmClient ssmClient;

    @NonNull
    public <T> Optional<T> getParameter(String parameterName, Class<T> clazz) {
        String json = getParameter( parameterName );
        if (StringUtils.hasText( json )) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                return Optional.of( objectMapper.readValue( json, clazz ) );
            } catch (JsonProcessingException e) {
                throw new PnInternalException( "Unable to deserialize object", ERROR_CODE_PN_GENERIC_ERROR, e );
            }
        }
        return Optional.empty();
    }

    private String getParameter(String parameterName) {
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(parameterName)
                .build();
        try {
            GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
            return parameterResponse.parameter().value();
        } catch ( SsmException ex) {
            log.info( "Ssm Client exception for parameterName={}", parameterName, ex );
            return null;
        }
    }
}
