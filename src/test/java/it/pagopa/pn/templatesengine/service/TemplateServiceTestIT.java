package it.pagopa.pn.templatesengine.service;

import it.pagopa.pn.templatesengine.config.BaseTest;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.Emailbody;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.LanguageEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TemplateServiceTestIT extends BaseTest {

    @Autowired
    TemplateService templateService;

    @Test
    void executeTextTemplate() {
        // Arrange
        Emailbody emailbody = new Emailbody();
        emailbody.setVerificationCode("VerificationCode");

        // Genera un elenco di 10 richieste
        List<Mono<String>> calls = IntStream.range(0, 10)
                .mapToObj(i -> templateService.executeTextTemplate(
                        TemplatesEnum.EMAIL_BODY,
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
                    Assertions.assertEquals(10, results.size(), "Expected 10 results");
                    results.forEach(result ->
                            Assertions.assertTrue(result.contains("VerificationCode"),
                                    "Each result should include the verification code"));
                })
                .verifyComplete();
    }

    @Test
    void executePdfTemplate() {
        // Arrange
        Emailbody emailbody = new Emailbody();
        emailbody.setVerificationCode("VerificationCode");

        // Genera un elenco di 10 richieste
        List<Mono<byte[]>> calls = IntStream.range(0, 10)
                .mapToObj(i -> templateService.executePdfTemplate(
                        TemplatesEnum.EMAIL_BODY,
                        LanguageEnum.IT,
                        Mono.just(emailbody)
                )).collect(Collectors.toList());

        // Esegue le richieste in parallelo e raccoglie i risultati
        Mono<List<byte[]>> parallelResults = Flux.fromIterable(calls)
                .flatMap(Function.identity()) // Converte la lista di Mono in un Flux
                .parallel() // Attiva il parallelismo
                .runOn(Schedulers.boundedElastic()) // Scheduler per task IO-bound
                .sequential() // Torna al flusso sequenziale dopo l'elaborazione
                .collectList();

        // Assert
        StepVerifier.create(parallelResults)
                .assertNext(results ->
                        Assertions.assertEquals(10, results.size(), "Expected 10 results")
                )
                .verifyComplete();
    }

    @Test
    void executeTemplateNoBody() {
        // Genera un elenco di 10 richieste
        List<Mono<String>> calls = IntStream.range(0, 10)
                .mapToObj(i -> templateService.executeTextTemplate(
                        TemplatesEnum.PEC_SUBJECT_REJECT,
                        LanguageEnum.IT
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
                    Assertions.assertEquals(10, results.size(), "Expected 10 results");
                    results.forEach(result ->
                            Assertions.assertTrue(result.contains("SEND - La PEC che hai inserito non è valida"),
                                    "Each result should include the verification code"));
                }).verifyComplete();
    }

}
