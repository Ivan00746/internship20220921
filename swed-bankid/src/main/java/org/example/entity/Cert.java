package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cert {
    private String notBefore;
    private String notAfter;

    public String getNotBefore() {
        return notBefore;
    }

    public String getNotAfter() {
        return notAfter;
    }
}
