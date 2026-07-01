package it.pagopa.pn.templatesengine.model;

import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * Parametri processati in output per i template con contenuto HTML generato da Markdown.
 * Accessibili nel template FreeMarker come ${GENERATED.primaryContentHtml}, ecc.
 */
@Getter
@Setter
public class InformalCommunicationGeneratedParams implements MarkdownToHtmlProcessor.OutputModel {
    private String primaryContentHtml;
    private String secondaryContentHtml;
}