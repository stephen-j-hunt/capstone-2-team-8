package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private int transferId;
    private int senderId;
    private int receiverId;
    private boolean isRequest;
    private boolean status;
    private BigDecimal amount;
    private LocalDateTime dateTime;

    public Transaction(){};
    public Transaction(int transferId, int senderId, int receiverId, boolean isRequest, boolean status, BigDecimal amount, LocalDateTime dateTime) {
        this.transferId = transferId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isRequest = isRequest;
        this.status = status;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
