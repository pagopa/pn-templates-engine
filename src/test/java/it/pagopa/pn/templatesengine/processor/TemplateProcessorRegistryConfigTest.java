package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplateProcessorRegistryConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunication;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationSender;
import it.pagopa.pn.templatesengine.model.InformalAnalogCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoProcessor;
import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

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
        lenient().when(markdownToHtmlProcessor.process(any(), any(), any())).thenReturn(Mono.empty());
        when(senderLogoProcessor.process(any(), any(), any())).thenReturn(Mono.empty());

        registryConfig.init();

        var model = new InformalCommunication();
        model.setSender(new InformalCommunicationSender().id("9a7c1b23-46a3-489b-8ed4-398ffb32b45a"));

        for (TemplatesEnum template : new TemplatesEnum[]{
                TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION
        }) {
            StepVerifier.create(registry.executeProcessors(template, model))
                    .assertNext(out -> {
                        assertNotNull(out);
                        assertInstanceOf(InformalAnalogCommunicationGeneratedParams.class, out);
                    })
                    .verifyComplete();
        }
    }
}