package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.PnDocumentComposition;
import it.pagopa.pn.templates.engine.utils.PnTemplateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@AllArgsConstructor
public class PnTemplateService {

  private final PnDocumentComposition pnDocumentComposition;

  public byte[] executePdfTemplate(String templateName, Object model) {
    log.info("Execute Pdf templateName={} START", templateName);
    var map = PnTemplateUtils.dtoToMap(model);
    return pnDocumentComposition.executePdfTemplate(templateName, map);
  }

  public String executeTextTemplate(String templateName, Object model) {
    log.info("Execute Text templateName={} START", templateName);
    var map = PnTemplateUtils.dtoToMap(model);
    return pnDocumentComposition.executeTextTemplate(templateName, map);
  }

}
