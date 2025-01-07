package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Getter
@Setter
@ToString
@Configuration
@EnableScheduling
@ConfigurationProperties(prefix = "pn.templatesengine")
@Import(SharedAutoConfiguration.class)
@Slf4j
public class PnTemplatesEngineConfig {
}
