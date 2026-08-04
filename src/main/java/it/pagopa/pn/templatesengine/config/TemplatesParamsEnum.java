package it.pagopa.pn.templatesengine.config;

import lombok.Getter;

@Getter
public enum TemplatesParamsEnum {

    SENDER_LOGO("senderLogo");

    private final String value;

    TemplatesParamsEnum(String value) {
        this.value = value;
    }

}
