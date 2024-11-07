package it.pagopa.pn.templates.engine.exceptions;

import lombok.Getter;


@Getter
public enum ExceptionTypeEnum {
    ERROR_FREEMARKER_BEAN_CONFIGURATION( "ERROR_FREEMARKER_BEAN_CONFIGURATION", "Errore durante la creazione del bean di configurazione FreeMarker"),
    ERROR_TEMPLATES_DOCUMENT_COMPOSITION( "ERROR_TEMPLATES_DOCUMENT_COMPOSITION", "Errore durante la generazione del template");

    private final String title;
    private final String message;

    ExceptionTypeEnum(String title, String message) {
        this.title = title;
        this.message = message;
    }
}