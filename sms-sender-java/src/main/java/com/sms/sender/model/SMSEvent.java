package com.sms.sender.model;

public class SMSEvent {
    private String userId;
    private String phoneNumber;
    private String message;
    private String status;

    public SMSEvent() {}

    public SMSEvent(String userId, String phoneNumber, String message, String status) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.message = message;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
