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
    public Mono<ResponseEntity<Resource>> notificationReceivedLegalFact(String xLanguage,
                                                                        Mono<NotificationReceiverLegalFact> notificationReceiverLegalFact,
                                                                        final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> pecDeliveryWorkflowLegalFact(String xLanguage,
                                                                       Mono<PecDeliveryWorkflowLegalFact> pecDeliveryWorkflowLegalFact,
                                                                       final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationViewedLegalFact(String xLanguage,
                                                                      Mono<NotificationViewedLegalFact> notificationViewedLegalFact,
                                                                      final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> pdfLegalFact(String xLanguage,
                                                       Mono<PdfLegalFact> pdfLegalFact,
                                                       final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationCancelledLegalFact(String xLanguage,
                                                                         Mono<NotificationCancelledLegalFact> notificationCancelledLegalFact,
                                                                         final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationAAR(String xLanguage,
                                                          Mono<NotificationAAR> notificationAAR,
                                                          final ServerWebExchange exchange) {
        return Mono.empty();
    }
}
