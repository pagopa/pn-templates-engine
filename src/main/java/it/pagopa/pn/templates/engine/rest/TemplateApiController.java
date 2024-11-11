package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.generated.openapi.server.v1.api.PdfTemplateApi;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templates.engine.mapper.TemplateMapper;
import it.pagopa.pn.templates.engine.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;


@Slf4j
@RestController
public class TemplateApiController implements PdfTemplateApi {
    private final TemplateService templateService;

    public TemplateApiController(TemplateService templateService) {
        this.templateService = templateService;
    }


    @Override
    public Mono<ResponseEntity<org.springframework.core.io.Resource>> notificationAAR(LanguageEnum xLanguage, Mono<NotificationAAR> notificationAAR,  final ServerWebExchange exchange) {
        String templateName = "notificationAAR";
        return TemplateMapper.getMapByDto(notificationAAR)
                .flatMap(mappedTemplate -> templateService.genNotificationAAR(templateName, xLanguage.getValue(), Mono.just(mappedTemplate)))
                .map(htmlText -> {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(htmlText.getBytes(StandardCharsets.UTF_8));
                    return ResponseEntity.accepted().body(new InputStreamResource(inputStream));
                });
    }

    @Override
    public Mono<ResponseEntity<org.springframework.core.io.Resource>> notificationCancelledLegalFact(LanguageEnum xLanguage, Mono<NotificationCancelledLegalFact> notificationCancelledLegalFact,  final ServerWebExchange exchange) {
        String templateName = "notificationCancelledLegalFact";
        return TemplateMapper.getMapByDto(notificationCancelledLegalFact)
                .flatMap(mappedTemplate -> templateService.executePdfTemplate(templateName, xLanguage.getValue(), Mono.just(mappedTemplate)))
                .map(pdf -> {
                    Resource resource = new ByteArrayResource(pdf);
                    return ResponseEntity.accepted().body(resource);
                });
    }

}