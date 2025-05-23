package it.pagopa.pn.templatesengine.exceptions;

import lombok.Getter;


@Getter
public enum ExceptionTypeEnum {
    ERROR_FREEMARKER_BEAN_CONFIGURATION( "ERROR_FREEMARKER_BEAN_CONFIGURATION", "Errore durante la creazione del bean di configurazione FreeMarker."),
    ERROR_TEMPLATES_DOCUMENT_COMPOSITION( "ERROR_TEMPLATES_DOCUMENT_COMPOSITION", "Errore durante la generazione del template."),
    ERROR_PDF_DOCUMENT_GENERATION( "ERROR_PDF_DOCUMENT_GENERATION", "Errore durante la generazione del pdf."),
    ERROR_OBJECT_MAPPING( "ERROR_OBJECT_MAPPING", "Errore durante la mappatura di un oggetto."),
    ERROR_FILE_READING( "ERROR_IMAGE_READING", "Errore durante la lettura del file."),
    ERROR_TEMPLATE_LOADING("ERROR_LOAD_TEMPLATE", "Errore durante la lettura dei template."),
    TEMPLATE_NOT_FOUND_FOR_LANGUAGE("TEMPLATE_NOT_FOUND", "Template not found for the selected language"),
    RESOLVER_PARAM_NOT_FOUND("RESOLVER_PARAM_NOT_FOUND", "Resolver param not found");

    private final String title;
    private final String message;

    ExceptionTypeEnum(String title, String message) {
        this.title = title;
        this.message = message;
    }
}