package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import it.pagopa.pn.templates.engine.utils.TemplateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.Map;


@Slf4j
@Service
public class TemplateService {
  private final DocumentComposition documentComposition;
  private final TemplateConfig templateConfig;
  private final String base64FileContent;

  @Autowired
  public TemplateService(DocumentComposition documentComposition,
                         TemplateConfig templateConfig,
                         @Qualifier("base64FileContent") String base64FileContent) {
    this.documentComposition = documentComposition;
    this.templateConfig = templateConfig;
    this.base64FileContent = base64FileContent;
  }


  public Mono<String> genNotificationAAR(String templateName, String language, Mono<Map<String, Object>> mappedTemplateMono) {
    String templateFileName = getFileName(templateName, language);
    return mappedTemplateMono
            .doOnNext(value -> printLog(templateFileName, language))
            .flatMap(mappedTemplate -> executeTextTemplate(
                    templateFileName,
                    Mono.just(TemplateUtils.appendLogoToMap(mappedTemplate, base64FileContent))));
  }

  private Mono<String> executeTextTemplate(String templateFileName, Mono<Map<String, Object>> mapTemplateModel) {
    log.info("Execute Text for templateName={} START", templateFileName);
    return documentComposition.executeTextTemplate(templateFileName, mapTemplateModel);
  }

  public Mono<byte[]> executePdfTemplate(String templateName, String language, Mono<Map<String, Object>> mapTemplateModel) {
    log.info("Execute Pdf for templateName={} START", templateName);
    String templateFileName = getFileName(templateName, language);
    printLog(templateFileName, language);
    return documentComposition.executePdfTemplate(templateFileName, mapTemplateModel);
  }

  private String getFileName(String templateName, String language) {
    TemplateConfig.Template template = templateConfig.getTemplates().get(templateName);
    return template.getInput().get(language);
  }

  private void printLog(String templateFileName, String language) {
    log.info("Creation of {} for {} language and file name {}", templateFileName.replace(".html", ""), language, templateFileName);
  }
}