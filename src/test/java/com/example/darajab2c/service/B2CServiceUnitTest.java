package com.example.darajab2c.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.repository.B2CResponseRepository;

import java.util.Optional;

public class B2CServiceUnitTest {

    @InjectMocks
    private B2CService b2cService;

    @Mock
    private B2CRequestRepository b2cRequestRepository;

    @Mock
    private B2CResponseRepository b2cResponseRepository;

    @Mock
    private KafkaTemplate<String, B2CRequest> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetPaymentStatus() {
        String originatorConversationID = "test123";
        B2CRequest mockRequest = new B2CRequest();
        mockRequest.setOriginatorConversationID(originatorConversationID);

        when(b2cRequestRepository.findByOriginatorConversationID(originatorConversationID))
                .thenReturn(Optional.of(mockRequest));

        Optional<B2CRequest> result = b2cService.getPaymentStatus(originatorConversationID);

        assertTrue(result.isPresent());
        assertEquals(originatorConversationID, result.get().getOriginatorConversationID());
    }

    @Test
    void testUpdatePaymentStatus() {
        String originatorConversationID = "test123";
        String status = "COMPLETED";

        b2cService.updatePaymentStatus(originatorConversationID, status);

        verify(b2cRequestRepository).updateStatusByOriginatorConversationID(originatorConversationID, status);
    }

    @Test
    void testUpdatePaymentStatusInvalidStatus() {
        String originatorConversationID = "test123";
        String status = "INVALID_STATUS";

        assertThrows(IllegalArgumentException.class, () -> {
            b2cService.updatePaymentStatus(originatorConversationID, status);
        });
    }

    @Test
    void testSendB2CRequestToKafka() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("test123");

        b2cService.sendB2CRequestToKafka(request);

        verify(kafkaTemplate).send("b2c-requests", request.getOriginatorConversationID(), request);
    }
}