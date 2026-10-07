package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

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

        // Act & Assert
        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Hello <strong>world</strong></p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Second <em>content</em></p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldKeepSecondaryContentHtmlNull_WhenSecondaryContentIsNull() {
        // Arrange
        InformalCommunicationBody body = new InformalCommunicationBody("# Titolo");

        // Act & Assert
        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<h1>Titolo</h1>\n", outParams.getPrimaryContentHtml());
        assertNull(outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldConvertSingleNewLineIntoBr() {
        InformalCommunicationBody body = new InformalCommunicationBody("Riga1\nRiga2")
                .secondaryContent("Sec1\nSec2");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Riga1<br />Riga2</p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Sec1<br />Sec2</p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldCreateNewParagraph_WhenTwoNewLines() {
        InformalCommunicationBody body = new InformalCommunicationBody("Riga1\n\nRiga2")
                .secondaryContent("Sec1\n\nSec2");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Riga1</p>\n<p>Riga2</p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Sec1</p>\n<p>Sec2</p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldAddOneBrForEachExtraNewLine_WhenThreeOrMoreNewLines() {
        InformalCommunicationBody body = new InformalCommunicationBody("Riga1\n\n\nRiga2")
                .secondaryContent("Sec1\n\n\n\n\nSec2");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Riga1<br /></p>\n<p>Riga2</p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Sec1<br /><br /><br /></p>\n<p>Sec2</p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldKeepBulletLists() {
        InformalCommunicationBody body = new InformalCommunicationBody("Elenco:\n\n- uno\n- due\n\nFine")
                .secondaryContent("Elenco:\n\n- uno\n- due\n\n\nFine");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Elenco:</p>\n<ul>\n<li>uno</li>\n<li>due</li>\n</ul>\n<p>Fine</p>\n",
                outParams.getPrimaryContentHtml());
        assertEquals("<p>Elenco:</p>\n<ul>\n<li>uno</li>\n<li>due<br /></li>\n</ul>\n<p>Fine</p>\n",
                outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldKeepBlockStructure_WhenThreeOrMoreNewLinesPrecedeHeadingOrList() {
        InformalCommunicationBody body = new InformalCommunicationBody("Testo\n\n\n## Titolo")
                .secondaryContent("Testo\n\n\n- uno\n- due");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Testo<br /></p>\n<h2>Titolo</h2>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Testo<br /></p>\n<ul>\n<li>uno</li>\n<li>due</li>\n</ul>\n",
                outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldRenderBrTagsFromInput_WithoutEscapingThem() {
        InformalCommunicationBody body = new InformalCommunicationBody("Riga1<br />Riga2<br><br/>Riga3")
                .secondaryContent("Sec1<BR />Sec2");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Riga1<br />Riga2<br /><br />Riga3</p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p>Sec1<br />Sec2</p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldKeepEscapingOtherHtmlTags() {
        InformalCommunicationBody body = new InformalCommunicationBody("Testo <script>alert(1)</script>");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Testo &lt;script&gt;alert(1)&lt;/script&gt;</p>\n", outParams.getPrimaryContentHtml());
    }

    @Test
    void process_ShouldSetHtmlFieldsToNull_WhenBodyContentsAreNull() {        // Arrange
        InformalCommunicationBody body = new InformalCommunicationBody();
        body.setPrimaryContent(null);
        body.setSecondaryContent(null);

        // Act & Assert
        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertNull(outParams.getPrimaryContentHtml());
        assertNull(outParams.getSecondaryContentHtml());
    }
}
