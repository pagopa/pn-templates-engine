package it.pagopa.pn.templates.engine.component;

import reactor.core.publisher.Mono;

import java.util.Map;


public interface DocumentComposition {
    String executeTextTemplate(String templateFileName, Map<String, Object> mapTemplateModel);
    byte[] executePdfTemplate(String templateFileName, Map<String, Object> mapTemplateModel);
}