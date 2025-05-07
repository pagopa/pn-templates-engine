package it.pagopa.pn.templatesengine.resolver;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ResolverEnum {
    TO_BASE64_RESOLVER("TO_BASE64_RESOLVER"),
    QRCODE_RESOLVER("QRCODE_RESOLVER");

    private final String value;

    ResolverEnum(String value) {
        this.value = value;
    }

    public static ResolverEnum fromValue(String value) {
        return Arrays.stream(values())
                .filter(e -> e.value.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
