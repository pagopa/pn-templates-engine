package it.pagopa.pn.templates.engine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;


@Data
@Configuration
@ConfigurationProperties(prefix = "templates")
public class PnTemplateConfig {
  @Value("${resources.templates.path}")
  private String templatePath;
  private List<Template> templates;

  @Data
  private static class Template {
    private String name;
    private Map<String, String> input;
  }
}