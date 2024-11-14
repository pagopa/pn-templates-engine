package it.pagopa.pn.templates.engine.service;

import it.pagopa.pn.templates.engine.component.DocumentComposition;
import it.pagopa.pn.templates.engine.config.TemplateConfig;
import it.pagopa.pn.templates.engine.exceptions.ExceptionTypeEnum;
import it.pagopa.pn.templates.engine.exceptions.PnGenericException;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.LanguageEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class TemplateService {

    private final DocumentComposition documentComposition;
    private final TemplateConfig templateConfig;

    public Mono<String> executeTextTemplate(String templateName, LanguageEnum language, Object objectModel) {
        log.info("Execute TXT for templateName={},  language={} - START", templateName, language);
        return Mono.justOrEmpty(getFileName(templateName, language))
                .flatMap(templateFileName -> Mono.justOrEmpty(documentComposition.executeTextTemplate(templateFileName, objectModel)))
                .switchIfEmpty(Mono.error(new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, templateName)));
    }

    public Mono<byte[]> executePdfTemplate(String templateName, LanguageEnum language, Object objectModel) {
        log.info("Execute Pdf for templateName={},  language={} - START", templateName, language);
        return Mono.justOrEmpty(getFileName(templateName, language))
                .flatMap(templateFileName -> Mono.justOrEmpty(documentComposition.executePdfTemplate(templateFileName, objectModel)))
                .switchIfEmpty(Mono.error(new PnGenericException(ExceptionTypeEnum.TEMPLATE_NOT_FOUND, templateName)));
    }

    private String getFileName(String templateName, LanguageEnum language) {
        TemplateConfig.Template template = templateConfig.getTemplates().get(templateName);
        return template.getInput().get(language.getValue());
    }

    public Mono<String> executeTextTemplate(String templateName, LanguageEnum language) {
        log.info("Execute templateAsString for templateName={},  language={} - START", templateName, language);
        TemplateConfig.Template template = templateConfig.getTemplatesAsString().get(templateName);
        return Mono.just(template.getInput().get(language.getValue()));
    }
}