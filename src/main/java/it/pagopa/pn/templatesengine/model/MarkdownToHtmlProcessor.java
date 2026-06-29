package it.pagopa.pn.templatesengine.model;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.model.params.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import org.springframework.stereotype.Component;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

@Component
public class MarkdownToHtmlProcessor
        implements TemplateModelProcessor<InformalCommunicationBody, InformalCommunicationGeneratedParams> {

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    @Override
    public void process(InformalCommunicationBody body, InformalCommunicationGeneratedParams outParams) {
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