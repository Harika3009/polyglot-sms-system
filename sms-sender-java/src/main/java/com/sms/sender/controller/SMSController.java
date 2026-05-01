package com.sms.sender.controller;

import com.sms.sender.model.SMSRequest;
import com.sms.sender.model.SMSResponse;
import com.sms.sender.service.SMSService;
import com.sms.sender.service.KafkaProducerService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import com.sms.sender.service.RedisService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/sms")
public class SMSController {

    @Autowired
    private SMSService smsService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @PostMapping("/send")
    public SMSResponse sendSMS(@RequestBody SMSRequest request) {

        System.out.println("UserID received: " + request.getUserId());

        if (redisService.isBlocked(request.getUserId())) {
            return new SMSResponse("BLOCKED", "User is blocked");
        }

        String status = Math.random() > 0.5 ? "SUCCESS" : "FAIL";

        Map<String, String> event = new HashMap<>();
        event.put("userId", request.getUserId());
        event.put("phoneNumber", request.getPhoneNumber());
        event.put("message", request.getMessage());
        event.put("status", status);

        kafkaTemplate.send("sms-topic", event.toString());

        try {
            restTemplate.postForObject(
                "http://localhost:8081/internal/store",
                event,
                String.class
            );
        } catch (Exception e) {
            System.out.println("Go service error: " + e.getMessage());
        }

        return new SMSResponse(status, "SMS processed and sent to Kafka");
    }

    @Autowired
    private RedisService redisService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private RestTemplate restTemplate = new RestTemplate();
}