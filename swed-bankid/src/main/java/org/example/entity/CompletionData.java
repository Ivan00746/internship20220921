package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionData {
    private User user;
    private Device device;
    private Cert cert;
    private String signature;
    private String ocspResponse;

    public CompletionData() {
    }

    public User getUser() {
        return user;
    }

    public Device getDevice() {
        return device;
    }

    public Cert getCert() {
        return cert;
    }

    public String getSignature() {
        return signature;
    }

    public String getOcspResponse() {
        return ocspResponse;
    }

    @Override
    public String toString() {
        return  "{\"user\":{\"personalNumber\":\"" + user.getPersonalNumber() +
                "\", \"name\":\"" + user.getName() +
                "\", \"givenName\":\"" + user.getGivenName() +
                "\", \"surname\":\"" + user.getSurname() + "\"},\n" +
                "   \"device\":{\"ipAddress\":\"" + device.getIpAddress() + "\"},\n" +
                "   \"cert\":{\"notBefore\":\"" + cert.getNotBefore() +
                "\", \"notAfter\":\"" + cert.getNotAfter() + "\"},\n" +
                "   \"signature\":\"" + signature + "\",\n" +
                "   \"ocspResponse\":\"" + ocspResponse + "\"}";
    }
}
