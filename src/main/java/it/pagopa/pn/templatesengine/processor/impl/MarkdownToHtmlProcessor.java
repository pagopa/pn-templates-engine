package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InformalCommunicationBody;
import it.pagopa.pn.templatesengine.processor.TemplateModelProcessor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Processore che converte il contenuto Markdown di una comunicazione bonaria in HTML.
 *
 * <p>Utilizza la libreria commonmark per il parsing e il rendering.
 * Opera su {@link InformalCommunicationBody} estraendo {@code primaryContent}
 * e {@code secondaryContent}, convertendoli in HTML e scrivendoli nell'output.</p>
 *
 * <p>Qualsiasi model di output che implementi {@link OutputModel} può essere
 * popolato da questo processore, indipendentemente dalla classe concreta.</p>
 *
 * <h2>Gestione degli a capo</h2>
 *
 * <p>Il comportamento di CommonMark sugli a capo viene riadattato alle specifiche di IO,
 * (https://developer.pagopa.it/it/app-io/guides/io-guida-tecnica/v7.3/risorse-utili/guida-al-markdown)
 * dove <b>ogni {@code \n} è un a capo</b> e quindi anche gli a capo consecutivi sono significativi.
 * Markdown, invece, collasserebbe qualsiasi sequenza di due o più a capo in un unico cambio di
 * paragrafo. La corrispondenza applicata è la seguente:</p>
 *
 * <ul>
 *   <li>{@code \n} &rarr; a capo ({@code <br />})</li>
 *   <li>{@code \n\n} &rarr; nuovo paragrafo (equivalente a 2 a capo)</li>
 *   <li>{@code \n\n\n} &rarr; nuovo paragrafo + 1 a capo</li>
 *   <li>{@code \n\n\n\n} &rarr; nuovo paragrafo + 2 a capo</li>
 *   <li>e cosi' via: ogni a capo oltre i primi due aggiunge un {@code <br />}</li>
 * </ul>
 *
 * <p>Restano valide tutte le altre costruzioni Markdown (titoli, elenchi puntati e numerati,
 * formattazione inline), mentre l'HTML presente nel contenuto viene escapato per non esporre
 * l'output a injection: l'unica eccezione e' il tag {@code <br>}, ammesso come a capo esplicito.</p>
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

    /** Segnaposto usato al posto di {@code <br />} durante il parsing, per non essere escapato. */
    private static final String LINE_BREAK_TOKEN = "\uE000LINEBREAK\uE000";
    private static final String LINE_BREAK_HTML = "<br />";
    /** Numero di a capo che Markdown interpreta come cambio di paragrafo. */
    private static final int PARAGRAPH_BREAK_NEW_LINES = 2;
    private static final String PARAGRAPH_BREAK = "\n\n";
    private static final Pattern EXTRA_NEW_LINES = Pattern.compile("\n{3,}");
    private static final Pattern BR_TAG = Pattern.compile("<br\\s*/?>", Pattern.CASE_INSENSITIVE);

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .escapeHtml(true)
            .softbreak(LINE_BREAK_HTML)
            .build();

    /**
     * Converte il contenuto Markdown del body in HTML e lo imposta nell'output.
     *
     * @param template  il template in fase di elaborazione (non utilizzato direttamente)
     * @param body      il body della comunicazione contenente {@code primaryContent} e {@code secondaryContent}
     * @param outParams l'oggetto output in cui impostare i campi HTML
     * @return un Mono che completa immediatamente dopo aver popolato l'output
     */
    @Override
    public Mono<Void> process(TemplatesEnum template, InformalCommunicationBody body, OutputModel outParams) {
        outParams.setPrimaryContentHtml(toHtml(body.getPrimaryContent()));
        outParams.setSecondaryContentHtml(toHtml(body.getSecondaryContent()));
        return Mono.empty();
    }

    /**
     * Converte il Markdown in HTML applicando le regole sugli a capo descritte a livello di classe.
     *
     * @param markdown il contenuto Markdown da convertire, eventualmente {@code null}
     * @return l'HTML corrispondente, oppure {@code null} se il contenuto in ingresso e' {@code null}
     */
    private String toHtml(String markdown) {
        if (markdown == null) {
            return null;
        }

        Node document = parser.parse(normalizeLineBreaks(markdown));
        return renderer.render(document).replace(LINE_BREAK_TOKEN, LINE_BREAK_HTML);
    }

    /**
     * Sostituisce con {@link #LINE_BREAK_TOKEN} gli a capo che Markdown non renderebbe come
     * {@code <br />}, ovvero i tag {@code <br>} scritti nel contenuto (che altrimenti verrebbero
     * escapati e mostrati come testo) e gli a capo eccedenti il cambio di paragrafo.
     *
     * <p>Il token viene riconvertito in {@code <br />} solo a rendering concluso, cosi' da
     * attraversare intatto il parsing e l'escaping HTML.</p>
     */
    private String normalizeLineBreaks(String markdown) {
        String withBrTokens = BR_TAG.matcher(markdown).replaceAll(Matcher.quoteReplacement(LINE_BREAK_TOKEN));
        return expandExtraNewLines(withBrTokens);
    }

    /**
     * Espande le sequenze di tre o piu' a capo consecutivi in un cambio di paragrafo piu' un
     * {@link #LINE_BREAK_TOKEN} per ogni a capo eccedente i primi due.
     *
     * <p>I token vengono accodati al blocco che precede la sequenza, in modo che il blocco
     * successivo (titolo, elenco, ...) resti a inizio riga e quindi riconoscibile da Markdown.</p>
     */
    private String expandExtraNewLines(String markdown) {
        Matcher matcher = EXTRA_NEW_LINES.matcher(markdown);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            int extraNewLines = matcher.group().length() - PARAGRAPH_BREAK_NEW_LINES;
            String replacement = LINE_BREAK_TOKEN.repeat(extraNewLines) + PARAGRAPH_BREAK;
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}