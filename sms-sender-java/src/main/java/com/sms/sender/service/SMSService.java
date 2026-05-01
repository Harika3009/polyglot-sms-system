package com.sms.sender.service;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class SMSService {

    public String sendSMS(String phoneNumber, String message) {

        // Mock SMS sending
        Random random = new Random();

        boolean success = random.nextBoolean();

        if (success) {
            return "SUCCESS";
        } else {
            return "FAIL";
        }
    }
}