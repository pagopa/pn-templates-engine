package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MarkdownToHtmlProcessorTest {

    private MarkdownToHtmlProcessor markdownToHtmlProcessor;
    private InformalCommunicationGeneratedParams outParams;

    @BeforeEach
    void setUp() {
        markdownToHtmlProcessor = new MarkdownToHtmlProcessor();
        outParams = new InformalCommunicationGeneratedParams();
    }

    @Test
    void process_ShouldConvertPrimaryAndSecondaryMarkdownToHtml() {
        // Arrange
        InformalCommunicationBody body = new InformalCommunicationBody("Hello **world**")
                .secondaryContent("Second *content*");

        // Act
        markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams);

        // Assert
        assertEquals("<p>Hello <strong>world</strong></p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Second <em>content</em></p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldKeepSecondaryContentHtmlNull_WhenSecondaryContentIsNull() {
        // Arrange
        InformalCommunicationBody body = new InformalCommunicationBody("# Titolo");

        // Act
        markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams);

        // Assert
        assertEquals("<h1>Titolo</h1>\n", outParams.getPrimaryContentHtml());
        assertNull(outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldSetHtmlFieldsToNull_WhenBodyContentsAreNull() {
        // Arrange
        InformalCommunicationBody body = new InformalCommunicationBody();
        body.setPrimaryContent(null);
        body.setSecondaryContent(null);

        // Act
        markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams);

        // Assert
        assertNull(outParams.getPrimaryContentHtml());
        assertNull(outParams.getSecondaryContentHtml());
    }
}
