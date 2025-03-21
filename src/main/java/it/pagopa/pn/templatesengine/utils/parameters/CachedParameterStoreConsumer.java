package it.pagopa.pn.templatesengine.utils.parameters;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ssm.SsmClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CachedParameterStoreConsumer extends ParameterStoreConsumer {
    private static final Duration DEFAULT_CACHE_TTL = Duration.ofMinutes(10);

    private final Map<String, CachedValue<?>> cache = new ConcurrentHashMap<>();
    private final Duration cacheTtl = DEFAULT_CACHE_TTL;

    public CachedParameterStoreConsumer(SsmClient ssmClient){
        super(ssmClient);
    }


    @NotNull
    @Override
    public <T> Optional<T> getParameter(String parameterName, Class<T> clazz) {
        CachedValue<?> cachedValue = cache.get(parameterName);
        var now = Instant.now();

        // Controllo se il valore è ancora valido
        if (cachedValue != null && Duration.between(cachedValue.timestamp, now).compareTo(cacheTtl) < 0) {
            // Il valore in cache deve essere del tipo richiesto
            if (clazz.isInstance(cachedValue.value)) {
                return Optional.of(clazz.cast(cachedValue.value));
            } else {
                log.warn("Incompatible cached data type for {}: expected {}, found {}",
                        parameterName, clazz.getSimpleName(), cachedValue.value.getClass().getSimpleName());
                cache.remove(parameterName); // Rimuoviamo l'elemento corrotto
            }
        }

        Optional<T> fetchedValue = super.getParameter(parameterName, clazz);
        fetchedValue.ifPresent(value -> cache.put(parameterName, new CachedValue<>(value, now)));

        return fetchedValue;
    }

    private record CachedValue<T>(T value, Instant timestamp) {
    }
}