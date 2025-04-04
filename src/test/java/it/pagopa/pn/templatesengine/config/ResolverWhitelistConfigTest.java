package it.pagopa.pn.templatesengine.config;

import it.pagopa.pn.templatesengine.utils.parameters.CachedParameterStoreConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolverWhitelistConfigTest {

    @Mock
    private TemplateConfig templateConfig;

    @Mock
    private CachedParameterStoreConsumer parameterStoreConsumer;

    @InjectMocks
    private ResolverWhitelistConfig resolverWhitelistConfig;

    @BeforeEach
    void setUp() {
        resolverWhitelistConfig = new ResolverWhitelistConfig(templateConfig, parameterStoreConsumer);
    }

    @Test
    void testInitializeWhitelist() {
        // Arrange
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR_RADDALT;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;
        String[] expectedValues = {"value1", "value2"};

        var templateC = new TemplateConfig.Template();
        var resolver = new TemplateConfig.Resolver();
        resolver.setWhitelistEnabled(true);
        resolver.setWhitelistParameterStores(List.of(expectedValues));
        templateC.setResolvers(
                Map.of(param, resolver)
        );
        var templates = Map.of(template, templateC);

        when(templateConfig.getTemplates()).thenReturn(templates);
        when(parameterStoreConsumer.getParameter(anyString(), eq(String[].class)))
                .thenReturn(Optional.of(expectedValues));

        // Act
        resolverWhitelistConfig.initializeWhitelist();

        // Assert
        assertTrue(resolverWhitelistConfig.isInWhitelist(template, param, "value1"));
        assertTrue(resolverWhitelistConfig.isInWhitelist(template, param, "value2"));
        assertFalse(resolverWhitelistConfig.isInWhitelist(template, param, "value3"));
    }

    @Test
    void testIsInWhitelistWhenEmpty() {
        TemplatesEnum template = TemplatesEnum.NOTIFICATION_AAR;
        TemplatesParamsEnum param = TemplatesParamsEnum.SENDER_LOGO_BASE64;

        assertFalse(resolverWhitelistConfig.isInWhitelist(template, param, "randomValue"));
    }
}
