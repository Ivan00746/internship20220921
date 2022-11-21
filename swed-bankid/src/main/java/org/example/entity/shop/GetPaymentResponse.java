package org.example.entity.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPaymentResponse {
    private PaymentOrder paymentOrder;
    private Operation[] operations;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentOrder {
        private String id;
        private String created;
        private String updated;
        private String operation;
        private String status;
        private String currency;
        private int amount;
        private int vatAmount;
        private int remainingCaptureAmount;
        private int remainingCancellationAmount;
        private String description;
        private String payerReference;
        private String initiatingSystemUserAgent;
        private String language;
        private String[] availableInstruments;
        private String implementation;
        private String integration;
        private Boolean instrumentMode;
        private Boolean guestMode;
        private IdContainer orderItems;
        private IdContainer urls;
        private IdContainer payeeInfo;
        private IdContainer payer;
        private IdContainer history;
        private IdContainer failed;
        private IdContainer aborted;
        private IdContainer paid;
        private IdContainer cancelled;
        private IdContainer financialTransactions;
        private IdContainer failedAttempts;
        private IdContainer metadata;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class IdContainer {
            private String id;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Operation {
        private String href;
        private String rel;
        private String method;
        private String contentType;

    }
}
