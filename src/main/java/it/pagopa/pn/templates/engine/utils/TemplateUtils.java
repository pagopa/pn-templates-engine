package it.pagopa.pn.templates.engine.utils;

import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum.ERROR_FILE_READING;

/**
 * Classe di utilità per la gestione dei file di template, come il caricamento dei template e l'ottenimento dei loro percorsi.
 * <p>
 * Questa classe fornisce metodi per ottenere il percorso assoluto di un file template e per caricare il contenuto di un template come stringa.
 * </p>
 * <p>
 * I metodi si basano su {@link ClassPathResource} di Spring per accedere alle risorse nel classpath.
 * </p>
 */
@Slf4j
public class TemplateUtils {

    private TemplateUtils() {
    }

    /**
     * Recupera il percorso formattato per un dato percorso relativo.
     * <p>
     * Il metodo risolve il percorso relativo fornito in un percorso assoluto e lo converte in una stringa URI.
     * Il prefisso "file:///" viene sostituito con "file:/" per renderlo un URI valido.
     * </p>
     *
     * @param relativePath il percorso relativo del template.
     * @return il percorso URI come stringa.
     * @throws PnGenericException se si verifica un errore durante la lettura del file o la risoluzione del percorso.
     */
    public static String getFormattedPath(String relativePath) {
        try {
            String absolutePath = new ClassPathResource(relativePath).getFile().getAbsolutePath();
            String uriPath = Paths.get(absolutePath).toUri().toString();
            return uriPath.replaceFirst("file:///", "file:/");
        } catch (Exception exception) {
            throw new PnGenericException(ERROR_FILE_READING, exception.getMessage());
        }
    }

    /**
     * Carica il contenuto di un file template dal classpath.
     * <p>
     * Il metodo legge il contenuto del file al percorso fornito come array di byte, lo converte in stringa e lo restituisce.
     * Il contenuto viene letto utilizzando la codifica UTF-8.
     * </p>
     *
     * @param templateFilePath il percorso relativo del file template.
     * @return il contenuto del template come stringa.
     * @throws PnGenericException se si verifica un errore durante il caricamento del template.
     */
    public static String loadTemplateContent(String templateFilePath) {
        try (InputStream inputStream = new ClassPathResource(templateFilePath).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATE_LOADING, e.getMessage());
        }
    }

}