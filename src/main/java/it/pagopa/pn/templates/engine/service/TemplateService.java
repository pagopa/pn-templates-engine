package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@Service
public class TemplateService {

    private final DocumentComposition documentComposition;
    private final TemplateConfig templateConfig;

    public TemplateService(DocumentComposition documentComposition, TemplateConfig templateConfig) {
        this.documentComposition = documentComposition;
        this.templateConfig = templateConfig;
    }

    public Mono<String> executeTextTemplate(String templateName, String language, Object objectModel) {
        log.info("Execute Text for templateName={},  language={} - START", templateName, language);
        String templateFileName = getFileName(templateName, language);
        return Mono.just(documentComposition.executeTextTemplate(templateFileName, objectModel));
    }

    public Mono<byte[]> executePdfTemplate(String templateName, String language, Object objectModel) {
        log.info("Execute Pdf for templateName={},  language={} - START", templateName, language);
        String templateFileName = getFileName(templateName, language);
        return Mono.just(documentComposition.executePdfTemplate(templateFileName, objectModel));
    }

    private String getFileName(String templateName, String language) {
        TemplateConfig.Template template = templateConfig.getTemplates().get(templateName);
        return template.getInput().get(language);
    }

    public Mono<String> executeTextTemplate(String templateName, String language) {
        TemplateConfig.Template template = templateConfig.getTemplatesAsString().get(templateName);
        return Mono.just(template.getInput().get(language));
    }
}