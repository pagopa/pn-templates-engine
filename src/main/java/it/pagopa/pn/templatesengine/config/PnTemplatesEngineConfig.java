package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.time.Duration;

@Slf4j
@Data
@ToString
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "pn.templates-engine")
@Import(SharedAutoConfiguration.class)
public class PnTemplatesEngineConfig {
    private Duration parameterStoreCacheTTL;
    private Duration urlResolverTimeout;

    @PostConstruct
    public void printConfig() {
        var sb = new StringBuilder();
        sb.append("\n************************* MICROSERVICE CONFIG *************************\n");
        sb.append(this);
        sb.append("\n***********************************************************************");
        log.info(sb.toString());
    }
}
