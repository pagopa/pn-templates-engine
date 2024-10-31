package it.pagopa.pn.template.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("template")
public class TemplateConfig {

  private String path;


}
