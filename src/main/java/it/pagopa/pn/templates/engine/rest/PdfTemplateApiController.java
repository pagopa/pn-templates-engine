package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.generated.openapi.server.v1.api.PdfTemplateApi;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
public class PdfTemplateApiController implements PdfTemplateApi {

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
    public Mono<ResponseEntity<Resource>> pdfLegalFact(LanguageEnum xLanguage,
                                                       Mono<PdfLegalFact> request,
                                                       final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationCancelledLegalFact(LanguageEnum xLanguage,
                                                                         Mono<NotificationCancelledLegalFact> request,
                                                                         final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAAR(LanguageEnum xLanguage,
                                                          Mono<NotificationAAR> request,
                                                          final ServerWebExchange exchange) {
        return Mono.empty();
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

}
