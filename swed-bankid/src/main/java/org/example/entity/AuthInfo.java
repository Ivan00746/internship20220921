package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthInfo {

    private String orderRef;
    private String autoStartToken;
    private String qrStartToken;
    private String qrStartSecret;

    public AuthInfo() {
    }

    public String getOrderRef() {
        return orderRef;
    }

    public String getAutoStartToken() {
        return autoStartToken;
    }

    public String getQrStartToken() {
        return qrStartToken;
    }

    public String getQrStartSecret() {
        return qrStartSecret;
    }
}
