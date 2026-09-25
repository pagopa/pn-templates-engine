package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void process_ShouldDropUrlsWithNotAllowedScheme() {
        InformalCommunicationBody body = new InformalCommunicationBody("[clicca](javascript:alert(1))")
                .secondaryContent("![logo](vbscript:msgbox)");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p><a rel=\"nofollow\" href=\"\">clicca</a></p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p><img src=\"\" alt=\"logo\" /></p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldRenderMarkdownLinkAndImage() {
        InformalCommunicationBody body = new InformalCommunicationBody(
                "Vai al [portale](https://www.notifichedigitali.it/ \"Titolo\")")
                .secondaryContent("![logo PN](https://cdn.pagopa.it/logo.png \"Logo\")");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>Vai al <a rel=\"nofollow\" href=\"https://www.notifichedigitali.it/\" title=\"Titolo\">portale</a></p>\n",
                outParams.getPrimaryContentHtml());
        assertEquals("<p><img src=\"https://cdn.pagopa.it/logo.png\" alt=\"logo PN\" title=\"Logo\" /></p>\n",
                outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldEscapeRawAnchorAndImgTags() {
        InformalCommunicationBody body = new InformalCommunicationBody(
                "<a href=\"javascript:alert(1)\">clicca</a>")
                .secondaryContent("<img src=\"x\" onerror=\"alert(1)\" />");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p>&lt;a href=&quot;javascript:alert(1)&quot;&gt;clicca&lt;/a&gt;</p>\n",
                outParams.getPrimaryContentHtml());
        assertEquals("<p>&lt;img src=&quot;x&quot; onerror=&quot;alert(1)&quot; /&gt;</p>\n",
                outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldDropDataUriImageAndAutolinkWithNotAllowedScheme() {
        InformalCommunicationBody body = new InformalCommunicationBody(
                "![x](data:text/html;base64,PHNjcmlwdD5hbGVydCgxKTwvc2NyaXB0Pg==)")
                .secondaryContent("[apri](file:///etc/passwd)");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p><img src=\"\" alt=\"x\" /></p>\n", outParams.getPrimaryContentHtml());
        assertEquals("<p><a rel=\"nofollow\" href=\"\">apri</a></p>\n", outParams.getSecondaryContentHtml());
    }

    @Test
    void process_ShouldDropJavascriptUrlsInAllObfuscatedForms() {
        InformalCommunicationBody body = new InformalCommunicationBody(
                "[a](javascript:alert(1)) [b](JaVaScRiPt:alert(1)) [c](  javascript:alert(1))")
                .secondaryContent("![d](java\tscript:alert(1)) [e](&#106;avascript:alert(1))");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        String primary = outParams.getPrimaryContentHtml();
        String secondary = outParams.getSecondaryContentHtml();
        assertFalse(primary.toLowerCase().contains("javascript"), primary);
        assertFalse(secondary.toLowerCase().contains("javascript"), secondary);
        assertEquals("<p><a rel=\"nofollow\" href=\"\">a</a> <a rel=\"nofollow\" href=\"\">b</a> <a rel=\"nofollow\" href=\"\">c</a></p>\n", primary);
    }

    @Test
    void process_ShouldKeepUrlsWithAllowedScheme() {
        InformalCommunicationBody body = new InformalCommunicationBody(
                "[sito](https://www.notifichedigitali.it/) [scrivi](mailto:info@pagopa.it) [relativo](/faq)");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        String html = outParams.getPrimaryContentHtml();
        assertTrue(html.contains("href=\"https://www.notifichedigitali.it/\""), html);
        assertTrue(html.contains("href=\"mailto:info@pagopa.it\""), html);
        assertTrue(html.contains("href=\"/faq\""), html);
    }

    @Test
    void process_ShouldKeepTelUrls() {
        InformalCommunicationBody body = new InformalCommunicationBody("[chiama](tel:+390612345678)");

        StepVerifier.create(markdownToHtmlProcessor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, body, outParams))
                .verifyComplete();

        assertEquals("<p><a rel=\"nofollow\" href=\"tel:+390612345678\">chiama</a></p>\n",
                outParams.getPrimaryContentHtml());
    }
}
