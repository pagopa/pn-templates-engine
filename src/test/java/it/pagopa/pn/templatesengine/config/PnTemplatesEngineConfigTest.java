package it.pagopa.pn.templatesengine.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PnTemplatesEngineConfigTest {

    @Test
    void shouldBindSenderLogoUrlTemplateFromFlatProperty() {
        var environment = new MockEnvironment()
                .withProperty("pn.templates-engine.sender-logo-url-template",
                        "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png");

        var config = Binder.get(environment)
                .bind("pn.templates-engine", Bindable.of(PnTemplatesEngineConfig.class))
                .orElseThrow(() -> new IllegalStateException("Unable to bind pn.templates-engine config"));

        assertEquals(
                "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png",
                config.getSenderLogoUrlTemplate()
        );
    }
}

