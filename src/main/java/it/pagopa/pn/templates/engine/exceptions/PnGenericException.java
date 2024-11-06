package it.pagopa.pn.templates.engine.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class PnGenericException extends RuntimeException {
    private final ExceptionTypeEnum exceptionType;
    private final HttpStatus httpStatus;
    private final String message;


    public PnGenericException(ExceptionTypeEnum exceptionType, String message){
        super(message);
        this.exceptionType = exceptionType;
        this.message = message;
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}