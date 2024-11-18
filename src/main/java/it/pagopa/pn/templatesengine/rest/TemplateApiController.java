package it.pagopa.pn.templatesengine.rest;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.api.TemplateApi;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templatesengine.service.TemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
public class TemplateApiController implements TemplateApi {

    private final TemplateService templateService;

    @Override
    public Mono<ResponseEntity<Resource>> notificationReceivedLegalFact(LanguageEnum xLanguage,
                                                                        Mono<NotificationReceiverLegalFact> request,
                                                                        final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage,
                                                                       Mono<PecDeliveryWorkflowLegalFact> request,
                                                                       final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationViewedLegalFact(LanguageEnum xLanguage,
                                                                      Mono<NotificationViewedLegalFact> request,
                                                                      final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> legalFactMalfunction(LanguageEnum xLanguage,
                                                               Mono<LegalFactMalfunction> request,
                                                               final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.LEGAL_FACT_MALFUNCTION, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationCancelledLegalFact(LanguageEnum xLanguage,
                                                                         Mono<NotificationCancelledLegalFact> request,
                                                                         final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAAR(LanguageEnum xLanguage,
                                                        Mono<NotificationAAR> request,
                                                        final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARRADDalt(LanguageEnum xLanguage,
                                                               Mono<NotificationAARRADDalt> request,
                                                               final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_RADDALT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage,
                                                                                 Mono<AnalogDeliveryWorkflowFailureLegalFact> request,
                                                                                 final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForEMAIL(LanguageEnum xLanguage,
                                                                Mono<NotificationAARForEMAIL> request,
                                                                final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForPEC(LanguageEnum xLanguage,
                                                              Mono<NotificationAARForPEC> request,
                                                              final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_PEC, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForSMS(LanguageEnum xLanguage,
                                                              Mono<NotificationAARForSMS> request,
                                                              final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_SMS, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> emailbody(LanguageEnum xLanguage,
                                                  Mono<Emailbody> request,
                                                  final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.EMAIL_BODY, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> pecbody(LanguageEnum xLanguage,
                                                Mono<Pecbody> request,
                                                final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_BODY, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> pecbodyconfirm(LanguageEnum xLanguage,
                                                       Mono<Pecbody> request,
                                                       final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_BODY_CONFIRM, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> pecbodyreject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_BODY_REJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARSubject(LanguageEnum xLanguage,
                                                               Mono<NotificationAARSubject> request,
                                                               final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_SUBJECT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> emailsubject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.EMAIL_SUBJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecsubject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_SUBJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecsubjectconfirm(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_SUBJECT_CONFIRM, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecsubjectreject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_SUBJECT_REJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> smsbody(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.SMS_BODY, xLanguage);
    }

    private <T> Mono<ResponseEntity<Resource>> processPdfTemplate(TemplatesEnum template, LanguageEnum xLanguage, Mono<T> request) {
        return templateService.executePdfTemplate(template, xLanguage, request)
                .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
    }

    private <T> Mono<ResponseEntity<String>> processTextTemplate(TemplatesEnum template, LanguageEnum xLanguage, Mono<T> request) {
        return templateService.executeTextTemplate(template, xLanguage, request)
                .map(result -> ResponseEntity.accepted().body(result));
    }

    private Mono<ResponseEntity<String>> processTextTemplate(TemplatesEnum template, LanguageEnum xLanguage) {
        return templateService.executeTextTemplate(template, xLanguage)
                .map(result -> ResponseEntity.accepted().body(result));
    }
}
