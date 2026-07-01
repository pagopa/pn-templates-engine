package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.model.params.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import org.springframework.stereotype.Component;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Component
public class MarkdownToHtmlProcessor
        implements TemplateModelProcessor<InformalCommunicationBody, MarkdownToHtmlProcessor.OutputModel> {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * Contratto per i model di output che supportano contenuto HTML
     * generato dalla conversione Markdown → HTML.
     */
    public interface OutputModel {
        void setPrimaryContentHtml(String html);
        void setSecondaryContentHtml(String html);
    }

    @Override
    public void process(InformalCommunicationBody body, OutputModel outParams) {
        outParams.setPrimaryContentHtml(toHtml(body.getPrimaryContent()));
        outParams.setSecondaryContentHtml(toHtml(body.getSecondaryContent()));
    }

    private String toHtml(String markdown) {
        if (markdown == null) {
            return null;
        }

        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}