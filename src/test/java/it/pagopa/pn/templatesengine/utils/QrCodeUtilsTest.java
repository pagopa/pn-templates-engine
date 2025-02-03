package it.pagopa.pn.templatesengine.utils;

import it.pagopa.pn.commons.exceptions.PnInternalException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class QrCodeUtilsTest {
    @Test
    void testGetQrCodeQuickAccessUrlAarDetail_Success() {
        String testUrl = "https://example.com";
        String result = QrCodeUtils.getQrCodeQuickAccessUrlAarDetail(testUrl);

        assertThat(result).isNotNull();
        assertThat(result).startsWith("data:image/png;base64, ");
        assertThat(result).contains("base64");
    }

    @Test
    void testGetQrCodeQuickAccessUrlAarDetail_NullUrl() {
        assertThatThrownBy(() -> QrCodeUtils.getQrCodeQuickAccessUrlAarDetail(null))
                .isInstanceOf(PnInternalException.class);
    }

    @Test
    void testGetQrCodeQuickAccessUrlAarDetail_EmptyUrl() {
        assertThatThrownBy(() -> QrCodeUtils.getQrCodeQuickAccessUrlAarDetail(""))
                .isInstanceOf(PnInternalException.class);
    }
}
