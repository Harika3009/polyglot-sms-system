package com.sms.sender.service;

import com.sms.sender.model.SMSEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "sms-topic";
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishSmsEvent(SMSEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
