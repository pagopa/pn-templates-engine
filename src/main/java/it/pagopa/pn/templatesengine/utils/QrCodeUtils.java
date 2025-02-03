package it.pagopa.pn.templatesengine.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static it.pagopa.pn.commons.exceptions.PnExceptionsCodes.ERROR_CODE_PN_GENERIC_ERROR;

public class QrCodeUtils {
    private QrCodeUtils() {}
    public static String getQrCodeQuickAccessUrlAarDetail(String url) {
        if (url == null || url.isEmpty())
            throw new PnInternalException("Url must not be null", ERROR_CODE_PN_GENERIC_ERROR);
        try {
            Map<EncodeHintType, ?> conf = Map.of(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H, EncodeHintType.QR_VERSION, 14);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 180, 180, conf);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return "data:image/png;base64, " .concat(Base64Utils.encodeToString(pngOutputStream.toByteArray()));
        } catch (IOException | WriterException e) {
            throw new PnInternalException(e.getMessage(), ERROR_CODE_PN_GENERIC_ERROR, e);
        }
    }
}
