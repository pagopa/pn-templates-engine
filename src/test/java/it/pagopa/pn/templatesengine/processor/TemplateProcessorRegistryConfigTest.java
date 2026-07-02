package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplateProcessorRegistryConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationSender;
import it.pagopa.pn.templatesengine.model.InformalAnalogCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoProcessor;
import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TemplateProcessorRegistryConfigTest {

    @Mock
    private MarkdownToHtmlProcessor markdownToHtmlProcessor;

    @Mock
    private SenderLogoProcessor senderLogoProcessor;

    private TemplateProcessorRegistry registry;
    private TemplateProcessorRegistryConfig registryConfig;

    @BeforeEach
    void setUp() {
        registry = new TemplateProcessorRegistry();
        registryConfig = new TemplateProcessorRegistryConfig(
                registry,
                markdownToHtmlProcessor,
                senderLogoProcessor
        );
    }

    @Test
    void init_ShouldRegisterInformalCommunicationChains() {
        registryConfig.init();

        var model = new InformalCommunication();
        model.setSender(new InformalCommunicationSender().paId("9a7c1b23-46a3-489b-8ed4-398ffb32b45a"));

        for (TemplatesEnum template : new TemplatesEnum[]{
                TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION
        }) {
            Object out = registry.executeProcessors(template, model);

            assertNotNull(out);
            assertInstanceOf(InformalAnalogCommunicationGeneratedParams.class, out);
        }

        Object ioOut = registry.executeProcessors(TemplatesEnum.INFORMAL_IO_COMMUNICATION, model);

        assertNotNull(ioOut);
        assertInstanceOf(InformalCommunicationGeneratedParams.class, ioOut);
    }
}