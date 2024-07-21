package com.example.darajab2c.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.darajab2c.service.B2CService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.repository.B2CResponseRepository;

import java.util.Optional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class B2CServiceIntegrationTest {

    @Autowired
    private B2CService b2cService;

    @Autowired
    private B2CRequestRepository b2cRequestRepository;

    @Autowired
    private B2CResponseRepository b2cResponseRepository;

    @MockBean
    private KafkaTemplate<String, B2CRequest> kafkaTemplate;

    @Test
    void testProcessB2CRequest() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("test123");
        request.setInitiatorName("TestInitiator");
        request.setSecurityCredential("TestCredential");
        request.setCommandID("BusinessPayment");
        request.setAmount(1000L);
        request.setPartyA(600000L);
        request.setPartyB(254712345678L);
        request.setRemarks("Test Payment");
        request.setQueueTimeOutURL("https://example.com/timeout");
        request.setResultURL("https://example.com/result");
        request.setOccasion("Test Occasion");

        B2CResponse response = b2cService.processB2CRequest(request);

        assertNotNull(response);
        assertNotNull(response.getConversationID());
        assertEquals(request.getOriginatorConversationID(), response.getOriginatorConversationID());
        assertNotNull(response.getResponseCode());
        assertNotNull(response.getResponseDescription());

        // Verify that the request and response were saved to the database
        assertTrue(b2cRequestRepository.findByOriginatorConversationID(request.getOriginatorConversationID()).isPresent());
        assertTrue(b2cResponseRepository.findByOriginatorConversationID(request.getOriginatorConversationID()).isPresent());
    }

    @Test
    void testGetAndUpdatePaymentStatus() {
        // First, create and save a B2CRequest
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("test456");
        request.setStatus("Pending");
        b2cRequestRepository.save(request);

        // Test getPaymentStatus
        Optional<B2CRequest> retrievedRequest = b2cService.getPaymentStatus("test456");
        assertTrue(retrievedRequest.isPresent());
        assertEquals("Pending", retrievedRequest.get().getStatus());

        // Test updatePaymentStatus
        b2cService.updatePaymentStatus("test456", "COMPLETED");

        // Verify the status was updated
        retrievedRequest = b2cService.getPaymentStatus("test456");
        assertTrue(retrievedRequest.isPresent());
        assertEquals("COMPLETED", retrievedRequest.get().getStatus());
    }
}