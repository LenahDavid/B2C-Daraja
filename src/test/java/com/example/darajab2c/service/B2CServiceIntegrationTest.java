package com.example.darajab2c.service;

import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.repository.B2CResponseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class B2CServiceIntegrationTest {

    @Autowired
    private B2CService b2cService;

    @Autowired
    private B2CRequestRepository b2cRequestRepository;

    @Autowired
    private B2CResponseRepository b2cResponseRepository;

    @BeforeEach
    void setUp() {
        b2cRequestRepository.deleteAll();
        b2cResponseRepository.deleteAll();
    }

    @Test
    void initiateB2CPayment_Integration() {
        String result = b2cService.initiateB2CPayment("testID", "testName", "credential", "command", 100L, 123L, 456L, "remarks", "queueUrl", "resultUrl", "occasion");

        assertNotNull(result);
        assertTrue(b2cRequestRepository.findByOriginatorConversationID("testID").isPresent());
    }

    @Test
    void processB2CRequest_Integration() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("testID");
        request.setInitiatorName("testName");
        // Set other fields...

        B2CResponse response = b2cService.processB2CRequest(request);

        assertNotNull(response);
        assertEquals("testID", response.getOriginatorConversationID());
        assertTrue(b2cRequestRepository.findByOriginatorConversationID("testID").isPresent());
    }

    @Test
    void getPaymentStatus_Integration() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("testID");
        b2cRequestRepository.save(request);

        Optional<B2CRequest> result = b2cService.getPaymentStatus("testID");

        assertTrue(result.isPresent());
        assertEquals("testID", result.get().getOriginatorConversationID());
    }

    @Test
    void updatePaymentStatus_Integration() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("testID");
        b2cRequestRepository.save(request);

        B2CResponse response = new B2CResponse();
        response.setOriginatorConversationID("testID");
        response.setResponseCode("0");

        b2cService.updatePaymentStatus(response);

        Optional<B2CRequest> updatedRequest = b2cRequestRepository.findByOriginatorConversationID("testID");
        assertTrue(updatedRequest.isPresent());
        assertEquals("COMPLETED", updatedRequest.get().getStatus());
    }
}