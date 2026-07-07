package it.pagopa.pn.templatesengine.component;

public interface DocumentComposition {

    String executeTextTemplate(String templateFileName, Object model, Object processedParams);

    byte[] executePdfTemplate(String templateFileName, Object model, Object processedParams);
}