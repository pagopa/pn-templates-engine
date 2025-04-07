package it.pagopa.pn.templatesengine.config;

import lombok.Getter;

@Getter
public enum TemplatesParamsEnum {

    SENDER_LOGO_BASE64("senderLogoBase64");

    private final String value;

    TemplatesParamsEnum(String value) {
        this.value = value;
    }

}
