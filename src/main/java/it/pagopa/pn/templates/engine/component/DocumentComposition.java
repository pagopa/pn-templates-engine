package it.pagopa.pn.templates.engine.component;

public interface DocumentComposition {

    String executeTextTemplate(String templateFileName, Object model);

    byte[] executePdfTemplate(String templateFileName, Object model);
}