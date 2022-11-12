package org.example.component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.example.service.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DynamicQR {
    @Autowired
    private DataStore dataStore;

    public BufferedImage generateQRImage() throws NoSuchAlgorithmException, InvalidKeyException, WriterException {
        if (dataStore.getAuthInfo() != null) {
            String qrStartToken = dataStore.getAuthInfo().getQrStartToken();
            String qrStartSecret = dataStore.getAuthInfo().getQrStartSecret();
            String qrTime = Long.toString(dataStore.getAuthResponseTime().until(Instant.now(), ChronoUnit.SECONDS));

            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(qrStartSecret.getBytes(StandardCharsets.US_ASCII), "HmacSHA256"));
            mac.update(qrTime.getBytes(StandardCharsets.US_ASCII));

            String qrAuthCode = String.format("%064x", new BigInteger(1, mac.doFinal()));
            String qrData = String.join(".", "bankid", qrStartToken, qrTime, qrAuthCode);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, 250, 250);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } return null;
    }
}
