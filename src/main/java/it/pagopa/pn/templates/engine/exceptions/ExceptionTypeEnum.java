package it.pagopa.pn.templates.engine.exceptions;

import lombok.Getter;


@Getter
public enum ExceptionTypeEnum {
    ERROR_TEMPLATES_DOCUMENT_COMPOSITION_FAILED( "ERROR_TEMPLATES_DOCUMENT_COMPOSITION_FAILED", "Errore durante la generazione del template");

    private final String title;
    private final String message;

    ExceptionTypeEnum(String title, String message) {
        this.title = title;
        this.message = message;
    }
}