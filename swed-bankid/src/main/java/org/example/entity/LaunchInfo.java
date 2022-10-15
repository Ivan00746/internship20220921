package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LaunchInfo {
    private String orderRef;
    private String autoStartToken;

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getAutoStartToken() {
        return autoStartToken;
    }

    public void setAutoStartToken(String autoStartToken) {
        this.autoStartToken = autoStartToken;
    }
}
