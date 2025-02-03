package it.pagopa.pn.templatesengine.rest;

import it.pagopa.pn.templatesengine.generated.openapi.server.v1.api.QrCodeGeneratorApi;
import it.pagopa.pn.templatesengine.generated.openapi.server.v1.dto.InlineResponse200;
import it.pagopa.pn.templatesengine.utils.QrCodeUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
public class QrCodeApiController implements QrCodeGeneratorApi {
    @Override
    public Mono<ResponseEntity<InlineResponse200>> qrcodegenerator(String url, final ServerWebExchange exchange) {
        InlineResponse200 res = new InlineResponse200();
        res.setBase64value(QrCodeUtils.getQrCodeQuickAccessUrlAarDetail(url));
        return Mono.just(ResponseEntity.ok().body(res));
    }
}
