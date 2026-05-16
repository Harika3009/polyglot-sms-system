package com.sms.sender.controller;

import com.sms.sender.model.SMSEvent;
import com.sms.sender.model.SMSRequest;
import com.sms.sender.model.SMSResponse;
import com.sms.sender.service.KafkaProducerService;
import com.sms.sender.service.RedisService;
import com.sms.sender.service.SMSService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/sms")
public class SMSController {

    private final SMSService smsService;
    private final KafkaProducerService kafkaProducerService;
    private final RedisService redisService;

    public SMSController(SMSService smsService, KafkaProducerService kafkaProducerService, RedisService redisService) {
        this.smsService = smsService;
        this.kafkaProducerService = kafkaProducerService;
        this.redisService = redisService;
    }

    @PostMapping("/send")
    public SMSResponse sendSMS(@RequestBody SMSRequest request) {

        if (redisService.isBlocked(request.getUserId())) {
            return new SMSResponse("BLOCKED", "User is blocked from sending SMS");
        }

        String status = smsService.sendSMS(request.getPhoneNumber(), request.getMessage());

        SMSEvent event = new SMSEvent(
                request.getUserId(),
                request.getPhoneNumber(),
                request.getMessage(),
                status
        );

        kafkaProducerService.publishSmsEvent(event);

        return new SMSResponse(status, "SMS processed successfully");
    }
}
