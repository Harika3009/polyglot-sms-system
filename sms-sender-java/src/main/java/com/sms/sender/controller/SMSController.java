package com.sms.sender.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sms")
public class SMSController {

    @PostMapping("/send")
    public String sendSMS() {
        return "SMS request received";
    }

}