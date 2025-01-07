package it.pagopa.pn.templatesengine.exceptions;

import it.pagopa.pn.commons.exceptions.PnRuntimeException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class DocumentCompositionException extends PnRuntimeException {

    public DocumentCompositionException(@NotNull String message, @NotNull String description, int status,
                                        @NotNull String errorCode, String element) {
        super(message, description, status, errorCode, element, description);
    }
}
