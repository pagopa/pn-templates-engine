package it.pagopa.pn.template.service;

import it.pagopa.pn.template.dao.FileDao;
import it.pagopa.pn.template.legalfacts.DocumentComposition;
import it.pagopa.pn.template.utils.TemplateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProcessTemplateService {

  private final FileDao fileDao;
  private final DocumentComposition documentComposition;

  public byte[] executePdfTemplate(String templateName, Object model) {
    log.info("Execute Pdf templateName={} START", templateName);
    var content = fileDao.getFile(templateName);
    var map = TemplateUtils.dtoToMap(model);
    return documentComposition.executePdfTemplate(content, map);
  }

  public String executeTextTemplate(String templateName, Object model) {
    log.info("Execute Text templateName={} START", templateName);
    var content = fileDao.getFile(templateName);
    var map = TemplateUtils.dtoToMap(model);
    return documentComposition.executeTextTemplate(content, map);
  }

}
