package it.pagopa.pn.templatesengine.model;

import it.pagopa.pn.templatesengine.processor.impl.MarkdownToHtmlProcessor;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoBase64Processor;
import it.pagopa.pn.templatesengine.processor.impl.SenderLogoUrlProcessor;
import lombok.Getter;
import lombok.Setter;

/**
 * Parametri processati in output per i template con contenuto HTML generato da Markdown.
 * Accessibili nel template FreeMarker come ${GENERATED.primaryContentHtml}, ecc.
 */
@Getter
@Setter
public class InformalCommunicationGeneratedParams
        implements MarkdownToHtmlProcessor.OutputModel,
                    SenderLogoBase64Processor.OutputModel,
                    SenderLogoUrlProcessor.OutputModel {
    private String primaryContentHtml;
    private String secondaryContentHtml;
    private String senderLogoBase64;
    private String senderLogoUrl;
}