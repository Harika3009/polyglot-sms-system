package com.sms.sender.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SMSServiceTest {

    private final SMSService smsService = new SMSService();

    @Test
    void shouldReturnSuccessOrFail() {
        String result = smsService.sendSMS("9999999999", "hello");
        assertTrue(result.equals("SUCCESS") || result.equals("FAIL"));
    }

    @Test
    void shouldNotReturnNullOrEmpty() {
        String result = smsService.sendSMS("9999999999", "hello");
        assertTrue(result != null && !result.isEmpty());
    }

    @Test
    void shouldReturnValidStatusAfterMultipleCalls() {
        for (int i = 0; i < 10; i++) {
            String result = smsService.sendSMS("9999999999", "hello");
            assertTrue(result.equals("SUCCESS") || result.equals("FAIL"),
                "Expected SUCCESS or FAIL but got: " + result);
        }
    }
}
