package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;


@Slf4j
@Service
public class TemplateService {
    private final DocumentComposition documentComposition;
    private final TemplateConfig templateConfig;
    private final String base64FileContent;


    public TemplateService(DocumentComposition documentComposition,
                           TemplateConfig templateConfig,
                           @Qualifier("base64FileContent") String base64FileContent) {
        this.documentComposition = documentComposition;
        this.templateConfig = templateConfig;
        this.base64FileContent = base64FileContent;
    }

    public Mono<String> executeTextTemplate(String templateName, String language, Map<String, Object> mapTemplateModel) {
        log.info("Execute Text for templateName={},  language={} - START", templateName, language);
        String templateFileName = getFileName(templateName, language);
        return Mono.just(documentComposition.executeTextTemplate(templateFileName, mapTemplateModel));
    }

    public Mono<byte[]> executePdfTemplate(String templateName, String language, Map<String, Object> mapTemplateModel) {
        log.info("Execute Pdf for templateName={},  language={} - START", templateName, language);
        String templateFileName = getFileName(templateName, language);
        return Mono.just(documentComposition.executePdfTemplate(templateFileName, mapTemplateModel));
    }

    private String getFileName(String templateName, String language) {
        TemplateConfig.Template template = templateConfig.getTemplates().get(templateName);
        return template.getInput().get(language);
    }
}