package com.sms.sender.model;

public class SMSRequest {

    private String userId;
    private String phoneNumber;
    private String message;

    public String getUserId() { return userId; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getMessage() { return message; }

    public void setUserId(String userId) { this.userId = userId; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setMessage(String message) { this.message = message; }
}