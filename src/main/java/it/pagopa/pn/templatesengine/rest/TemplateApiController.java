package it.pagopa.pn.templatesengine.rest;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.api.TemplateApi;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.*;
import it.pagopa.pn.templatesengine.resolver.TemplateValueResolver;
import it.pagopa.pn.templatesengine.service.TemplateService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@RestController
@AllArgsConstructor
public class TemplateApiController implements TemplateApi {

    private final TemplateService templateService;
    public final TemplateValueResolver templateValueResolver;

    @Override
    public Mono<ResponseEntity<Resource>> notificationReceivedLegalFact(
            LanguageEnum xLanguage,
            Mono<NotificationReceivedLegalFact> request,
            final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.NOTIFICATION_RECEIVED_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> pecDeliveryWorkflowLegalFact(
            LanguageEnum xLanguage,
            Mono<PecDeliveryWorkflowLegalFact> request,
            final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.PEC_DELIVERY_WORKFLOW_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationViewedLegalFact(
            LanguageEnum xLanguage,
            Mono<NotificationViewedLegalFact> request,
            final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.NOTIFICATION_VIEWED_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> malfunctionLegalFact(
            LanguageEnum xLanguage,
            Mono<MalfunctionLegalFact> request,
            final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.MALFUNCTION_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationCancelledLegalFact(
            LanguageEnum xLanguage,
            Mono<NotificationCancelledLegalFact> request,
            final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.NOTIFICATION_CANCELLED_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationAar(
            LanguageEnum xLanguage,
            Mono<NotificationAar> request,
            final ServerWebExchange exchange) {
        return request
                // Scarica l'immagine e la converte in base64
                .flatMap(r ->
                        resolveAndUpdateField(
                                r,
                                NotificationAar::getSenderLogoBase64,
                                NotificationAar::setSenderLogoBase64,
                                TemplatesEnum.NOTIFICATION_AAR,
                                TemplatesParamsEnum.SENDER_LOGO_BASE64
                        ))
                .flatMap(r ->
                        processPdfTemplate(TemplatesEnum.NOTIFICATION_AAR, xLanguage, Mono.just(r)));
    }

    @Override
    public Mono<ResponseEntity<Resource>> notificationAarRaddAlt(
            LanguageEnum xLanguage,
            Mono<NotificationAarRaddAlt> request,
            final ServerWebExchange exchange) {
        return request
                // Scarica l'immagine e la converte in base64
                .flatMap(r ->
                        resolveAndUpdateField(
                                r,
                                NotificationAarRaddAlt::getSenderLogoBase64,
                                NotificationAarRaddAlt::setSenderLogoBase64,
                                TemplatesEnum.NOTIFICATION_AAR_RADDALT,
                                TemplatesParamsEnum.SENDER_LOGO_BASE64
                        ))
                .flatMap(r ->
                        processPdfTemplate(TemplatesEnum.NOTIFICATION_AAR_RADDALT, xLanguage, Mono.just(r)));
    }

    @Override
    public Mono<ResponseEntity<Resource>> analogDeliveryWorkflowFailureLegalFact(
            LanguageEnum xLanguage,
            Mono<AnalogDeliveryWorkflowFailureLegalFact> request,
            final ServerWebExchange exchange) {
        return processPdfTemplate(TemplatesEnum.ANALOG_DELIVERY_WORKFLOW_FAILURE_LEGAL_FACT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAarForEmail(
            LanguageEnum xLanguage,
            Mono<NotificationAarForEmail> request,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_EMAIL, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAarForPec(
            LanguageEnum xLanguage,
            Mono<NotificationAarForPec> request,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_PEC, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAarForSms(
            LanguageEnum xLanguage,
            Mono<NotificationAarForSms> request,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_SMS, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> mailVerificationCodeBody(
            LanguageEnum xLanguage,
            Mono<MailVerificationCodeBody> request,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> pecVerificationCodeBody(
            LanguageEnum xLanguage,
            Mono<PecVerificationCodeBody> request,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_VERIFICATION_CODE_BODY, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> pecValidationContactsRejectBody(
            LanguageEnum xLanguage,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_BODY, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> notificationAarForSubject(
            LanguageEnum xLanguage,
            Mono<NotificationAarForSubject> request,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.NOTIFICATION_AAR_FOR_SUBJECT, xLanguage, request);
    }

    @Override
    public Mono<ResponseEntity<String>> mailVerificationCodeSubject(
            LanguageEnum xLanguage,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.MAIL_VERIFICATION_CODE_SUBJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecVerificationCodeSubject(
            LanguageEnum xLanguage,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_VERIFICATION_CODE_SUBJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecValidationContactsSuccessSubject(
            LanguageEnum xLanguage,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_SUBJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecValidationContactsSuccessBody(
            LanguageEnum xLanguage,
            ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_VALIDATION_CONTACTS_SUCCESS_BODY, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> pecValidationContactsRejectSubject(
            LanguageEnum xLanguage,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_SUBJECT, xLanguage);
    }

    @Override
    public Mono<ResponseEntity<String>> smsVerificationCodeBody(
            LanguageEnum xLanguage,
            final ServerWebExchange exchange) {
        return processTextTemplate(TemplatesEnum.SMS_VERIFICATION_CODE_BODY, xLanguage);
    }

    private <T> Mono<ResponseEntity<Resource>> processPdfTemplate(
            TemplatesEnum template,
            LanguageEnum xLanguage,
            Mono<T> request) {
        return templateService.executePdfTemplate(template, xLanguage, request)
                .map(resultBytes -> ResponseEntity.ok().body(new ByteArrayResource(resultBytes)));
    }

    private <T> Mono<ResponseEntity<String>> processTextTemplate(
            TemplatesEnum template,
            LanguageEnum xLanguage,
            Mono<T> request) {
        return templateService.executeTextTemplate(template, xLanguage, request)
                .map(result -> ResponseEntity.ok().body(result));
    }

    private Mono<ResponseEntity<String>> processTextTemplate(
            TemplatesEnum template,
            LanguageEnum xLanguage) {
        return templateService.executeTextTemplate(template, xLanguage)
                .map(result -> ResponseEntity.ok().body(result));
    }

    private <T> Mono<T> resolveAndUpdateField(
            T object,
            Function<T, String> getter,
            BiConsumer<T, String> setter,
            TemplatesEnum template,
            TemplatesParamsEnum param) {

        return templateValueResolver.resolve(getter.apply(object), template, param)
                .map(v -> {
                    setter.accept(object, v);
                    return object;
                })
                .switchIfEmpty(Mono.fromCallable(() -> { // Se resolve() è empty, assegna null
                    setter.accept(object,null);
                    return object;
                }));
    }
}
