package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

@Data
@ToString
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "pn.templates-engine")
@Import(SharedAutoConfiguration.class)
@Slf4j
public class PnTemplatesEngineConfig {
    private Duration parameterStoreCacheTTL;

    @EventListener
    public void printAllProperties(ContextRefreshedEvent contextRefreshedEvent) {
        var sb = new StringBuilder();
        sb.append("\n************************* ACTIVE PROPERTIES *************************\n");

        ((ConfigurableEnvironment) contextRefreshedEvent.getApplicationContext().getEnvironment())
                .getPropertySources()
                .stream()
                .filter(ps -> ps instanceof OriginTrackedMapPropertySource)
                .map(ps -> ((OriginTrackedMapPropertySource) ps).getSource().entrySet())
                .flatMap(Collection::stream)
                // Ordina per chiave
                .sorted(Map.Entry.comparingByKey())
                .forEach(property ->
                        sb.append(property.getKey())
                        .append("=")
                        .append(property.getValue()).append("\n"));

        sb.append("*********************************************************************");
        log.info(sb.toString());
    }
}
