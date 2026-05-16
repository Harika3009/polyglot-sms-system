package com.sms.sender.service;

import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class SMSService {

    private final Random random = new Random();

    public String sendSMS(String phoneNumber, String message) {
        return random.nextBoolean() ? "SUCCESS" : "FAIL";
    }
}
