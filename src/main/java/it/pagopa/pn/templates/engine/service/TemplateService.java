package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.utils.TemplateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class TemplateService {

  private final DocumentComposition documentComposition;

  public byte[] executePdfTemplate(String templateName, Object model) {
    log.info("Execute Pdf templateName={} START", templateName);
    var map = TemplateUtils.dtoToMap(model);
    return documentComposition.executePdfTemplate(templateName, map);
  }

  public String executeTextTemplate(String templateName, Object model) {
    log.info("Execute Text templateName={} START", templateName);
    var map = TemplateUtils.dtoToMap(model);
    return documentComposition.executeTextTemplate(templateName, map);
  }

}
