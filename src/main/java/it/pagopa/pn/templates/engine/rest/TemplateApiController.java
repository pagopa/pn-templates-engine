package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.generated.openapi.server.v1.api.TemplateApi;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templates.engine.mapper.TemplateMapper;
import it.pagopa.pn.templates.engine.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
public class TemplateApiController implements TemplateApi {
    private final TemplateService templateService;

    public TemplateApiController(TemplateService templateService) {
        this.templateService = templateService;
    }


    @Override
    public Mono<ResponseEntity<Resource>> notificationReceivedLegalFact(LanguageEnum xLanguage,
                                                                        Mono<NotificationReceiverLegalFact> request,
                                                                        final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage,
                                                                       Mono<PecDeliveryWorkflowLegalFact> request,
                                                                       final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationViewedLegalFact(LanguageEnum xLanguage,
                                                                      Mono<NotificationViewedLegalFact> request,
                                                                      final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> legalFactMalfunction(LanguageEnum xLanguage,
                                                               Mono<LegalFactMalfunction> request,
                                                               final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationCancelledLegalFact(LanguageEnum xLanguage,
                                                                         Mono<NotificationCancelledLegalFact> request,
                                                                         final ServerWebExchange exchange) {
        String templateName = "notificationCancelledLegalFact";
        return TemplateMapper.getMapByDto(request)
                .flatMap(mappedTemplate -> templateService.executePdfTemplate(templateName, xLanguage.getValue(), Mono.just(mappedTemplate)))
                .map(pdf -> {
                    Resource resource = new ByteArrayResource(pdf);
                    return ResponseEntity.accepted().body(resource);
                });
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAAR(LanguageEnum xLanguage,
                                                        Mono<NotificationAAR> request,
                                                        final ServerWebExchange exchange) {
        String templateName = "notificationAAR";
        return TemplateMapper.getMapByDto(request)
                .flatMap(mappedTemplate -> templateService.genNotificationAAR(templateName, xLanguage.getValue(), Mono.just(mappedTemplate)))
                .map(htmlText -> ResponseEntity.accepted().body(htmlText));
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARRADDalt(LanguageEnum xLanguage,
                                                               Mono<NotificationAARRADDalt> request,
                                                               final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage,
                                                                                 Mono<AnalogDeliveryWorkflowFailureLegalFact> request,
                                                                                 final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForEMAIL(LanguageEnum xLanguage,
                                                                Mono<NotificationAARForEMAIL> request,
                                                                final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForPEC(LanguageEnum xLanguage,
                                                              Mono<NotificationAARForPEC> request,
                                                              final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> emailbody(LanguageEnum xLanguage,
                                                  Mono<Emailbody> request,
                                                  final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> pecbody(LanguageEnum xLanguage,
                                                Mono<Pecbody> request,
                                                final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> pecbodyconfirm(LanguageEnum xLanguage,
                                                       Mono<Pecbody> request,
                                                       final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> pecbodyreject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARSubject(LanguageEnum xLanguage,
                                                               Mono<NotificationAAR> notificationAAR,
                                                               final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> emailsubject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> pecsubject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> pecsubjectconfirm(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> pecsubjectreject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return Mono.empty();
    }

}
