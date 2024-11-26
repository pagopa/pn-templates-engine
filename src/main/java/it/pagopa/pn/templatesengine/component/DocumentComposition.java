package it.pagopa.pn.templatesengine.component;

public interface DocumentComposition {

    String executeTextTemplate(String templateFileName, Object model);

    byte[] executePdfTemplate(String templateFileName, Object model);
}