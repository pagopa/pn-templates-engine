package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.MailVerificationCodeBody;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.MalfunctionLegalFact;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@SpringBootTest
class TemplateServiceTestIT {
    private final int NUM_REQUESTS = 100;

    @Autowired
    TemplateService templateService;

    @Test
    void executeTextTemplate() {
        // Arrange
        MailVerificationCodeBody emailbody = new MailVerificationCodeBody();
        emailbody.setVerificationCode("VerificationCode");

        // Genera un elenco di 10 richieste
        List<Mono<String>> calls = IntStream.range(0, NUM_REQUESTS)
                .mapToObj(i -> templateService.executeTextTemplate(
                        TemplatesEnum.MAIL_VERIFICATION_CODE_BODY,
                        LanguageEnum.IT,
                        Mono.just(emailbody)
                )).collect(Collectors.toList());

        // Esegue le richieste in parallelo e raccoglie i risultati
        Mono<List<String>> parallelResults = Flux.fromIterable(calls)
                .flatMap(Function.identity()) // Converte la lista di Mono in un Flux
                .parallel() // Attiva il parallelismo
                .runOn(Schedulers.boundedElastic()) // Scheduler per task IO-bound
                .sequential() // Torna al flusso sequenziale dopo l'elaborazione
                .collectList();

        // Assert
        StepVerifier.create(parallelResults)
                .assertNext(results -> {
                    Assertions.assertEquals(NUM_REQUESTS, results.size(), "Expected 10 results");
                    results.forEach(result ->
                            Assertions.assertTrue(result.contains("VerificationCode"),
                                    "Each result should include the verification code"));
                })
                .verifyComplete();
    }

    @Test
    void executePdfTemplate() {
        // Arrange
        var model = new MalfunctionLegalFact()
                .startDate("TEST_startDate")
                .timeReferenceStartDate("TEST_timeReferenceStartDate")
                .endDate("TEST_endDate")
                .timeReferenceEndDate("TEST_timeReferenceEndDate")
                .htmlDescription("<p>Sample <b>TEST_description</b></p>");

        // Genera un elenco di 100 richieste
        List<Mono<byte[]>> calls = IntStream.range(0, NUM_REQUESTS)
                .mapToObj(i -> templateService.executePdfTemplate(
                        TemplatesEnum.MALFUNCTION_LEGAL_FACT,
                        LanguageEnum.IT,
                        Mono.just(model)
                )).toList();

        // Esegue le richieste in parallelo e raccoglie i risultati
        Mono<List<byte[]>> parallelResults = Flux.fromIterable(calls)
                .flatMap(Function.identity()) // Converte la lista di Mono in un Flux
                .doOnError(error -> log.error("Errore nella pipeline Reactor", error))
                .parallel() // Attiva il parallelismo
                .runOn(Schedulers.boundedElastic()) // Scheduler per task IO-bound
                .sequential() // Torna al flusso sequenziale dopo l'elaborazione
                .collectList();

        // Assert
        StepVerifier.create(parallelResults)
                .assertNext(results ->
                        Assertions.assertEquals(NUM_REQUESTS, results.size(), "Expected 10 results")
                )
                .verifyComplete();
    }

    @Test
    void executeTemplateNoBody() {
        // Genera un elenco di 10 richieste
        List<Mono<String>> calls = IntStream.range(0, NUM_REQUESTS)
                .mapToObj(i -> templateService.executeTextTemplate(
                        TemplatesEnum.PEC_VALIDATION_CONTACTS_REJECT_SUBJECT,
                        LanguageEnum.IT
                )).toList();

        // Esegue le richieste in parallelo e raccoglie i risultati
        Mono<List<String>> parallelResults = Flux.fromIterable(calls)
                .flatMap(Function.identity()) // Converte la lista di Mono in un Flux
                .parallel() // Attiva il parallelismo
                .runOn(Schedulers.boundedElastic()) // Scheduler per task IO-bound
                .sequential() // Torna al flusso sequenziale dopo l'elaborazione
                .collectList();

        // Assert
        StepVerifier.create(parallelResults)
                .assertNext(results -> {
                    Assertions.assertEquals(NUM_REQUESTS, results.size(), "Expected 10 results");
                    results.forEach(result ->
                            Assertions.assertTrue(result.contains("SEND - La PEC che hai inserito non è valida"),
                                    "Each result should include the verification code"));
                }).verifyComplete();
    }

}
