package it.pagopa.pn.templatesengine.utils;

import it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templatesengine.exceptions.PnGenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static it.pagopa.pn.templatesengine.exceptions.ExceptionTypeEnum.ERROR_FILE_READING;

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
     * Recupera baseUri dati templatePath e templateFile
     *
     * @param templatesPath path direcotry templates
     * @param templateFile nome file template
     * @return il percorso URI come stringa.
     * @throws PnGenericException se si verifica un errore durante la lettura del file o la risoluzione del percorso.
     */
    public static String getBaseURI(String templatesPath, String templateFile ) {
        try {
            String relativePath = templatesPath + "/" + templateFile;
            URL templateUrl = Thread.currentThread().getContextClassLoader().getResource( relativePath );
            String baseUri = templateUrl.toString().replaceFirst("/[^/]*$", "/");
            return baseUri;
        } catch (Exception exception) {
            throw new PnGenericException(ERROR_FILE_READING, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
            throw new PnGenericException(ExceptionTypeEnum.ERROR_TEMPLATE_LOADING, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}