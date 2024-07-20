package com.example.darajab2c.service;

import com.example.darajab2c.controllers.GenerateToken;
import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.repository.B2CResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class B2CServiceTest {

    @Mock
    private B2CRequestRepository b2cRequestRepository;

    @Mock
    private B2CResponseRepository b2cResponseRepository;

    @Mock
    private KafkaTemplate<String, B2CRequest> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MpesaGenerateToken authService;

    @Mock
    private GenerateToken generateToken;

    @InjectMocks
    private B2CService b2cService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initiateB2CPayment_Success() {
        // Mock dependencies
        when(generateToken.generateToken()).thenReturn("{\"access_token\":\"test_token\"}");

        // Mock OkHttpClient (you might need to use PowerMockito for this)

        // Test
        String result = b2cService.initiateB2CPayment("testID", "testName", "credential", "command", 100L, 123L, 456L, "remarks", "queueUrl", "resultUrl", "occasion");

        // Verify
        assertNotNull(result);
        verify(b2cRequestRepository, times(1)).save(any(B2CRequest.class));
        verify(b2cResponseRepository, times(1)).save(any(B2CResponse.class));
    }

    @Test
    void processB2CRequest_Success() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("testID");

        when(b2cService.initiateB2CPayment(anyString(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("{\"ConversationID\":\"testConv\",\"OriginatorConversationID\":\"testID\",\"ResponseCode\":\"0\",\"ResponseDescription\":\"Success\"}");

        B2CResponse response = b2cService.processB2CRequest(request);

        assertNotNull(response);
        assertEquals("testID", response.getOriginatorConversationID());
        verify(kafkaTemplate, times(1)).send(eq("b2c-requests"), any(B2CRequest.class));
    }

    @Test
    void getPaymentStatus_Found() {
        B2CRequest request = new B2CRequest();
        when(b2cRequestRepository.findByOriginatorConversationID("testID")).thenReturn(Optional.of(request));

        Optional<B2CRequest> result = b2cService.getPaymentStatus("testID");

        assertTrue(result.isPresent());
    }

    @Test
    void updatePaymentStatus_Success() {
        B2CRequest request = new B2CRequest();
        B2CResponse response = new B2CResponse();
        response.setOriginatorConversationID("testID");
        response.setResponseCode("0");

        when(b2cRequestRepository.findById("testID")).thenReturn(Optional.of(request));

        b2cService.updatePaymentStatus(response);

        verify(b2cRequestRepository, times(1)).save(any(B2CRequest.class));
        assertEquals("COMPLETED", request.getStatus());
    }
}