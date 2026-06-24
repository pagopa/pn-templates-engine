package it.pagopa.pn.templatesengine.processor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TemplateProcessorRegistryConfigTest {

    @Mock
    private TemplateProcessorRegistry registry;

    private TemplateProcessorRegistryConfig registryConfig;

    @BeforeEach
    void setUp() {
        registryConfig = new TemplateProcessorRegistryConfig(registry);
    }

    @Test
    void constructor_ShouldCreateConfigurationWithoutRegisteringProcessors() {

    }
}
