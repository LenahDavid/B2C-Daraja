package com.example.darajab2c.service;

import com.example.darajab2c.entity.B2CResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class B2CKafkaConsumer {

    @Autowired
    private B2CService b2cService;
    private static final Logger logger = LoggerFactory.getLogger(B2CKafkaConsumer.class);

    @KafkaListener(topics = "b2c-responses", groupId = "b2c-group")
    public void listen(B2CResponse response) {
        logger.info("Received B2C response from Kafka: {}", response.getOriginatorConversationID());
        String status = response.getResponseCode().equals("0") ? "SUCCESS" : "FAILED";
        b2cService.updatePaymentStatus(response.getOriginatorConversationID(), status);
    }
}
