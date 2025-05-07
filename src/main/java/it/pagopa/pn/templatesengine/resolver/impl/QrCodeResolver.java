package it.pagopa.pn.templatesengine.resolver.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import it.pagopa.pn.templatesengine.resolver.Resolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;


@Slf4j
@Component
@RequiredArgsConstructor
public class QrCodeResolver implements Resolver<String, byte[]>{
    private static final int WIDTH = 180;
    private static final int HEIGHT = 180;
    private static final ErrorCorrectionLevel ERROR_CORRECTION_LEVEL = ErrorCorrectionLevel.H;
    private static final String IMAGE_FORMAT = "PNG";

    public Mono<byte[]> resolve(String paramValue) {
        return Mono.just(generateQRCodeImage(paramValue));
    }

    private byte[] generateQRCodeImage(String text) {
        try {
            Map<EncodeHintType, ?> conf = Map.of(EncodeHintType.ERROR_CORRECTION, QrCodeResolver.ERROR_CORRECTION_LEVEL,
                    EncodeHintType.QR_VERSION, 14);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text,
                    BarcodeFormat.QR_CODE, QrCodeResolver.WIDTH, QrCodeResolver.HEIGHT, conf);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();

            MatrixToImageWriter.writeToStream(bitMatrix, IMAGE_FORMAT, pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (IOException | WriterException e) {
            throw new PnInternalException(e.getMessage(), ERROR_CODE_PN_GENERIC_ERROR, e);
        }
    }
}
