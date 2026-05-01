package com.sms.sender.model;

public class SMSResponse {

    private String status;
    private String message;

    public SMSResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}