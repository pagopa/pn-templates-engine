package it.pagopa.pn.templatesengine.component;

import java.util.Map;

public interface DocumentComposition {

    String executeTextTemplate(String templateFileName, Object model, Map<String, String> processedParams);

    byte[] executePdfTemplate(String templateFileName, Object model, Map<String, String> processedParams);
}