package org.example.entity.shop.redundant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class CancelRequest {
    private Transaction transaction;

    public CancelRequest() {
        this.transaction = new Transaction();
    }

    public static class Transaction {
        private String description;
        private String payeeReference;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPayeeReference() {
            return payeeReference;
        }

        public void setPayeeReference(String payeeReference) {
            this.payeeReference = payeeReference;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "description='" + description + '\'' +
                    ", payeeReference='" + payeeReference + '\'' +
                    '}';
        }
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "CancelRequest{" +
                "transaction=" + transaction +
                '}';
    }
}
