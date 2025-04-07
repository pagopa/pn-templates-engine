package it.pagopa.pn.templatesengine.resolver;

import it.pagopa.pn.templatesengine.config.ResolverWhitelistConfig;
import it.pagopa.pn.templatesengine.config.TemplateConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.resolver.impl.ToBase64Resolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateValueResolverTest {

    @Mock
    private ToBase64Resolver toBase64Resolver;

    @Mock
    private TemplateConfig templateConfig;

    @Mock
    private ResolverWhitelistConfig whitelistConfig;

    private TemplateValueResolver templateValueResolver;

    @BeforeEach
    void setup() {
        templateValueResolver = new TemplateValueResolver(toBase64Resolver, templateConfig, whitelistConfig);
    }

    @Test
    void whenNoResolverConfigured_thenReturnOriginalValue() {
        // Arrange
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR_RADDALT;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;
        String input = "value";

        when(templateConfig.getTemplates()).thenReturn(Map.of(template, new TemplateConfig.Template()));

        // Act Assert
        StepVerifier.create(templateValueResolver.resolve(input, template, param))
                .expectNext(input)
                .verifyComplete();
    }

    @Test
    void shouldResolveUsingBase64Resolver() {
        // Arrange
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;
        String input = "TO_BASE64_RESOLVER:hello";
        String expectedOutput = "aGVsbG8=";

        var resolverConfig = new TemplateConfig.Resolver();
        resolverConfig.setEnabled(true);
        resolverConfig.setWhitelistEnabled(false);
        resolverConfig.setBypassAllWithNull(false);
        resolverConfig.setReturnNullOnError(true);
        var templateConf = new TemplateConfig.Template();
        templateConf.setResolvers(Map.of(param, resolverConfig));

        when(templateConfig.getTemplates()).thenReturn(Map.of(template, templateConf));
        when(toBase64Resolver.resolve("hello")).thenReturn(Mono.just(expectedOutput));

        // Act Assert
        StepVerifier.create(templateValueResolver.resolve(input, template, param))
                .expectNext(expectedOutput)
                .verifyComplete();
    }

    @Test
    void whenWhitelistFails_shouldReturnEmpty() {
        // Arrange
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR_RADDALT;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;
        String input = "TO_BASE64:hello";

        var resolverConfig = new TemplateConfig.Resolver();
        resolverConfig.setEnabled(true);
        resolverConfig.setWhitelistEnabled(true);
        resolverConfig.setBypassAllWithNull(false);
        resolverConfig.setReturnNullOnError(true);
        var templateConf = new TemplateConfig.Template();
        templateConf.setResolvers(Map.of(param, resolverConfig));

        when(templateConfig.getTemplates()).thenReturn(Map.of(template, templateConf));
        when(whitelistConfig.isInWhitelist(template, param, "hello")).thenReturn(false);

        // Act Assert
        StepVerifier.create(templateValueResolver.resolve(input, template, param))
                .verifyComplete();
    }

    @Test
    void shouldHandleResolverErrorGracefully() {
        // Arrange
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;
        String input = "TO_BASE64_RESOLVER:hello";

        var resolverConfig = new TemplateConfig.Resolver();
        resolverConfig.setEnabled(true);
        resolverConfig.setWhitelistEnabled(false);
        resolverConfig.setBypassAllWithNull(false);
        resolverConfig.setReturnNullOnError(true);
        var templateConf = new TemplateConfig.Template();
        templateConf.setResolvers(Map.of(param, resolverConfig));

        when(templateConfig.getTemplates()).thenReturn(Map.of(template, templateConf));
        when(toBase64Resolver.resolve("hello")).thenReturn(Mono.error(new RuntimeException("Resolver error")));

        // Act Assert
        StepVerifier.create(templateValueResolver.resolve(input, template, param))
                .verifyComplete();
    }

    @Test
    void onResolverErrorIfReturnNullOnErrorIsFalse_thenReturnOriginalValue() {
        // Arrange
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;
        String input = "TO_BASE64_RESOLVER:hello";

        var resolverConfig = new TemplateConfig.Resolver();
        resolverConfig.setEnabled(true);
        resolverConfig.setWhitelistEnabled(false);
        resolverConfig.setBypassAllWithNull(false);
        resolverConfig.setReturnNullOnError(false);
        var templateConf = new TemplateConfig.Template();
        templateConf.setResolvers(Map.of(param, resolverConfig));

        when(templateConfig.getTemplates()).thenReturn(Map.of(template, templateConf));
        when(toBase64Resolver.resolve("hello")).thenReturn(Mono.error(new RuntimeException("Resolver error")));

        StepVerifier.create(templateValueResolver.resolve(input, template, param))
                .expectNext(input)
                .verifyComplete();
    }
}
