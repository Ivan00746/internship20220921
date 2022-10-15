package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectInfo {

    private String orderRef;
    private String status;
    private String hintCode;
    private CompletionData completionData;

    public CollectInfo() {
    }

    public CompletionData getCompletionData() {
        return completionData;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHintCode() {
        return hintCode;
    }

    public void setHintCode(String hintCode) {
        this.hintCode = hintCode;
    }
}
