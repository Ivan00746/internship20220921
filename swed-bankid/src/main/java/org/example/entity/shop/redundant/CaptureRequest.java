package org.example.entity.shop.redundant;

import org.example.entity.shop.OrderItems;

import java.util.Arrays;

public class CaptureRequest {
    private Transaction transaction;

    public CaptureRequest() {
        this.transaction = new Transaction();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        return "CaptureRequest{" +
                "transaction=" + transaction +
                '}';
    }

    public static class Transaction {
        private String description;
        private int amount;
        private int vatAmount;
        private String payeeReference;
        private String receiptReference;
        private OrderItems[] orderItems;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getVatAmount() {
            return vatAmount;
        }

        public void setVatAmount(int vatAmount) {
            this.vatAmount = vatAmount;
        }

        public String getPayeeReference() {
            return payeeReference;
        }

        public void setPayeeReference(String payeeReference) {
            this.payeeReference = payeeReference;
        }

        public String getReceiptReference() {
            return receiptReference;
        }

        public void setReceiptReference(String receiptReference) {
            this.receiptReference = receiptReference;
        }

        public OrderItems[] getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(OrderItems[] orderItems) {
            this.orderItems = orderItems;
        }

        @Override
        public String toString() {
            return "Transaction{" +
                    "description='" + description + '\'' +
                    ", amount='" + amount + '\'' +
                    ", vatAmount='" + vatAmount + '\'' +
                    ", payeeReference='" + payeeReference + '\'' +
                    ", receiptReference='" + receiptReference + '\'' +
                    ", orderItems=" + Arrays.toString(orderItems) +
                    '}';
        }
    }
}

