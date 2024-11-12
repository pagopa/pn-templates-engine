package it.pagopa.pn.templates.engine.rest;

import it.pagopa.pn.templates.engine.config.TemplatesEnum;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.api.TemplateApi;
import it.pagopa.pn.templates.engine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templates.engine.mapper.TemplateMapper;
import it.pagopa.pn.templates.engine.service.TemplateService;
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
        return request.flatMap(notificationReceivedLegalFact ->
                TemplateMapper.getMapByDto(notificationReceivedLegalFact)
                        .flatMap(resulMap -> {
                            String templateName = TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT.getTemplate();
                            return templateService.executePdfTemplate(templateName, xLanguage.getValue(), resulMap)
                                    .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<Resource>> pecDeliveryWorkflowLegalFact(LanguageEnum xLanguage,
                                                                       Mono<PecDeliveryWorkflowLegalFact> request,
                                                                       final ServerWebExchange exchange) {
        return request.flatMap(pecDeliveryWorkflowLegalFact ->
                TemplateMapper.getMapByDto(pecDeliveryWorkflowLegalFact)
                        .flatMap(resulMap -> {
                            String templateName = TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT.getTemplate();
                            return templateService.executePdfTemplate(templateName, xLanguage.getValue(), resulMap)
                                    .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationViewedLegalFact(LanguageEnum xLanguage,
                                                                      Mono<NotificationViewedLegalFact> request,
                                                                      final ServerWebExchange exchange) {
        return request.flatMap(notificationViewedLegalFact ->
                TemplateMapper.getMapByDto(notificationViewedLegalFact)
                        .flatMap(resulMap -> {
                            String templateName = TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT.getTemplate();
                            return templateService.executePdfTemplate(templateName, xLanguage.getValue(), resulMap)
                                    .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<Resource>> legalFactMalfunction(LanguageEnum xLanguage,
                                                               Mono<LegalFactMalfunction> request,
                                                               final ServerWebExchange exchange) {
        return request.flatMap(legalFactMalfunction ->
                TemplateMapper.getMapByDto(legalFactMalfunction)
                        .flatMap(resulMap -> {
                            String templateName = TemplatesEnum.LEGAL_FACT_MALFUNCTION.getTemplate();
                            return templateService.executePdfTemplate(templateName, xLanguage.getValue(), resulMap)
                                    .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationCancelledLegalFact(LanguageEnum xLanguage,
                                                                         Mono<NotificationCancelledLegalFact> request,
                                                                         final ServerWebExchange exchange) {
        return request.flatMap(notificationCancelledLegalFact ->
                TemplateMapper.getMapByDto(notificationCancelledLegalFact)
                        .flatMap(resulMap -> {
                            String templateName = TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT.getTemplate();
                            return templateService.executePdfTemplate(templateName, xLanguage.getValue(), resulMap)
                                    .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAAR(LanguageEnum xLanguage,
                                                        Mono<NotificationAAR> request,
                                                        final ServerWebExchange exchange) {
        return request.flatMap(notificationAAR -> TemplateMapper.getMapByDto(notificationAAR)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.NOTIFICATION_AAR.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARRADDalt(LanguageEnum xLanguage,
                                                               Mono<NotificationAARRADDalt> request,
                                                               final ServerWebExchange exchange) {
        return request.flatMap(notificationAARRADDalt -> TemplateMapper.getMapByDto(notificationAARRADDalt)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.NOTIFICATION_AAR_RADDALT.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<Resource>> analogDeliveryWorkflowFailureLegalFact(LanguageEnum xLanguage,
                                                                                 Mono<AnalogDeliveryWorkflowFailureLegalFact> request,
                                                                                 final ServerWebExchange exchange) {
        return request.flatMap(analogDeliveryWorkflowFailureLegalFact ->
                TemplateMapper.getMapByDto(analogDeliveryWorkflowFailureLegalFact)
                        .flatMap(resulMap -> {
                            String templateName = TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT.getTemplate();
                            return templateService.executePdfTemplate(templateName, xLanguage.getValue(), resulMap)
                                    .map(resultBytes -> ResponseEntity.accepted().body(new ByteArrayResource(resultBytes)));
                        })
        );
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForEMAIL(LanguageEnum xLanguage,
                                                                Mono<NotificationAARForEMAIL> request,
                                                                final ServerWebExchange exchange) {
        return request.flatMap(notificationAARForEMAIL -> TemplateMapper.getMapByDto(notificationAARForEMAIL)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARForPEC(LanguageEnum xLanguage,
                                                              Mono<NotificationAARForPEC> request,
                                                              final ServerWebExchange exchange) {
        return request.flatMap(notificationAARForPEC -> TemplateMapper.getMapByDto(notificationAARForPEC)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.NOTIFICATION_AAR_FOR_PEC.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<String>> emailbody(LanguageEnum xLanguage,
                                                  Mono<Emailbody> request,
                                                  final ServerWebExchange exchange) {
        return request.flatMap(emailbody -> TemplateMapper.getMapByDto(emailbody)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.EMAIL_BODY.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<String>> pecbody(LanguageEnum xLanguage,
                                                Mono<Pecbody> request,
                                                final ServerWebExchange exchange) {
        return request.flatMap(pecbody -> TemplateMapper.getMapByDto(pecbody)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.PEC_BODY.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<String>> pecbodyconfirm(LanguageEnum xLanguage,
                                                       Mono<Pecbody> request,
                                                       final ServerWebExchange exchange) {
        return request.flatMap(pecbodyconfirm -> TemplateMapper.getMapByDto(pecbodyconfirm)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.PEC_BODY_CONFIRM.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
    }

    @Override
    public Mono<ResponseEntity<String>> pecbodyreject(LanguageEnum xLanguage, final ServerWebExchange exchange) {
        return Mono.empty();
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAARSubject(LanguageEnum xLanguage,
                                                               Mono<NotificationAAR> request,
                                                               final ServerWebExchange exchange) {
        return request.flatMap(notificationAARSubject -> TemplateMapper.getMapByDto(notificationAARSubject)
                .flatMap(dto -> {
                    String templateName = TemplatesEnum.NOTIFICATION_AAR_SUBJECT.getTemplate();
                    return templateService.executeTextTemplate(templateName, xLanguage.getValue(), dto);
                }).map(result -> ResponseEntity.accepted().body(result)));
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
