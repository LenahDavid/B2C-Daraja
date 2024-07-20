package com.example.darajab2c.service;

import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KafkaListeners {

    @Autowired
    private B2CRequestRepository requestRepository;

    @KafkaListener(topics = "b2c-responses", groupId = "b2c-group")
    public void listen(B2CResponse response) {
        Optional<B2CRequest> requestOpt = requestRepository.findById(response.getOriginatorConversationID());
        if (requestOpt.isPresent()) {
            B2CRequest request = requestOpt.get();
            if (response.getResponseCode().equals("0")) {
                request.setStatus("SUCCESS");
            } else {
                request.setStatus("FAILED");
            }
            requestRepository.save(request);
        }
    }
}
