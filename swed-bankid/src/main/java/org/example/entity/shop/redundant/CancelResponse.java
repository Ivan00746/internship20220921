package org.example.entity.shop.redundant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelResponse {
    private String payment;
    private Cancellation cancellation;
    public static class Cancellation {
        private String id;
        private Transaction transaction;
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Transaction {
            private String id;
            private String created;
            private String updated;
            private String type;
            private String state;
            private String number;
            private String amount;
            private String vatAmount;
            private String description;
            private String payeeReference;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getCreated() {
                return created;
            }

            public void setCreated(String created) {
                this.created = created;
            }

            public String getUpdated() {
                return updated;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getState() {
                return state;
            }

            public void setState(String state) {
                this.state = state;
            }

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getVatAmount() {
                return vatAmount;
            }

            public void setVatAmount(String vatAmount) {
                this.vatAmount = vatAmount;
            }

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
                        "id='" + id + '\'' +
                        ", created='" + created + '\'' +
                        ", updated='" + updated + '\'' +
                        ", type='" + type + '\'' +
                        ", state='" + state + '\'' +
                        ", number='" + number + '\'' +
                        ", amount='" + amount + '\'' +
                        ", vatAmount='" + vatAmount + '\'' +
                        ", description='" + description + '\'' +
                        ", payeeReference='" + payeeReference + '\'' +
                        '}';
            }
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public String toString() {
            return "Cancellation{" +
                    "id='" + id + '\'' +
                    ", transaction=" + transaction +
                    '}';
        }
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public Cancellation getCancellation() {
        return cancellation;
    }

    public void setCancellation(Cancellation cancellation) {
        this.cancellation = cancellation;
    }

    @Override
    public String toString() {
        return "CancelResponse{" +
                "payment='" + payment + '\'' +
                ", cancellation=" + cancellation +
                '}';
    }
}
