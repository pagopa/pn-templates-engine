package it.pagopa.pn.templatesengine.exceptions;

import it.pagopa.pn.commons.exceptions.PnValidationException;
import it.pagopa.pn.commons.exceptions.dto.ProblemError;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import lombok.Getter;

import java.util.List;

@Getter
public class TemplateNotFoundException extends PnValidationException {

    public TemplateNotFoundException(ExceptionTypeEnum exceptionTypeEnum, TemplatesEnum templatesEnum, String message) {
        super(message,
                List.of(
                        ProblemError.builder()
                                .code(exceptionTypeEnum.getTitle())
                                .detail(message)
                                .element(templatesEnum.getTemplate())
                                .build()
                )
        );
    }

}
