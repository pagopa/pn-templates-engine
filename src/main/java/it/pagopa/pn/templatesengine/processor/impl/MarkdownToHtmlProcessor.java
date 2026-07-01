package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import org.springframework.stereotype.Component;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

/**
 * Processore che converte il contenuto Markdown di una comunicazione bonaria in HTML.
 *
 * <p>Utilizza la libreria commonmark per il parsing e il rendering.
 * Opera su {@link InformalCommunicationBody} estraendo {@code primaryContent}
 * e {@code secondaryContent}, convertendoli in HTML e scrivendoli nell'output.</p>
 *
 * <p>Qualsiasi model di output che implementi {@link OutputModel} può essere
 * popolato da questo processore, indipendentemente dalla classe concreta.</p>
 */
@Component
public class MarkdownToHtmlProcessor
        implements TemplateModelProcessor<InformalCommunicationBody, MarkdownToHtmlProcessor.OutputModel> {

    /**
     * Contratto di output per i model che supportano contenuto HTML generato da Markdown.
     * Implementato dai Generated Params che espongono i campi HTML nel template FreeMarker.
     */
    public interface OutputModel {
        void setPrimaryContentHtml(String html);
        void setSecondaryContentHtml(String html);
    }

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    /**
     * Converte il contenuto Markdown del body in HTML e lo imposta nell'output.
     *
     * @param template  il template in fase di elaborazione (non utilizzato direttamente)
     * @param body      il body della comunicazione contenente {@code primaryContent} e {@code secondaryContent}
     * @param outParams l'oggetto output in cui impostare i campi HTML
     */
    @Override
    public void process(TemplatesEnum template, InformalCommunicationBody body, OutputModel outParams) {
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