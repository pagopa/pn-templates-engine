package it.pagopa.pn.templates.engine.component;

import java.util.Map;


public interface PnDocumentComposition {
    String executeTextTemplate(String content, Map<String, Object> mapTemplateModel);
    byte[] executePdfTemplate(String content, Map<String, Object> mapTemplateModel);
}