package it.pagopa.pn.template.service;

import it.pagopa.pn.template.dao.FileDao;
import it.pagopa.pn.template.legalfacts.impl.DocumentCompositionImpl;
import it.pagopa.pn.template.utils.TemplateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@AllArgsConstructor
public class ProcessorTemplateService {

  private final FileDao fileDao;
  private final DocumentCompositionImpl documentCompositionImpl;

  public Flux<Byte> executePdfTemplate(String templateName, Object model) {
    log.info("Execute Pdf templateName={} START", templateName);
    var content = fileDao.getFile(templateName);
    var map = TemplateUtils.dtoToMap(model);
    return documentCompositionImpl.executePdfTemplate(content, map);
  }

  public Mono<String> executeTextTemplate(String templateName, Object model) {
    log.info("Execute Text templateName={} START", templateName);
    var content = fileDao.getFile(templateName);
    var map = TemplateUtils.dtoToMap(model);
    return documentCompositionImpl.executeTextTemplate(content, map);
  }

}
