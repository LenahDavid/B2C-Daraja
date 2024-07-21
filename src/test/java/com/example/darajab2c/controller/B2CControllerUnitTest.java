package com.example.darajab2c.controller;

import com.example.darajab2c.controllers.B2CController;
import com.example.darajab2c.dto.PaymentRequest;
import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.service.B2CService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class B2CControllerUnitTest {
    @Mock
    private B2CService b2cService;

    @Mock
    private B2CRequestRepository b2cRequestRepository;

    @InjectMocks
    private B2CController b2cController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void receiveB2CRequest_Success() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOriginatorConversationID("testID");
        when(b2cService.initiateB2CPayment(anyString(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("Success");

        ResponseEntity<String> response = b2cController.receiveB2CRequest(paymentRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody());
    }

    @Test
    void receiveB2CRequest_Error() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOriginatorConversationID("testID");
        when(b2cService.initiateB2CPayment(anyString(), anyString(), anyString(), anyString(), anyLong(), anyLong(), anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new HttpStatusCodeException(HttpStatus.BAD_REQUEST, "Error") {
                });

        ResponseEntity<String> response = b2cController.receiveB2CRequest(paymentRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Error simulating B2C"));
    }

    @Test
    void getAllRequests_Success() {
        List<B2CRequest> requests = Arrays.asList(new B2CRequest(), new B2CRequest());
        when(b2cRequestRepository.findAll()).thenReturn(requests);

        List<B2CRequest> result = b2cController.getAllRequests();

        assertEquals(2, result.size());
    }

    @Test
    void fetchPaymentStatus_Found() {
        B2CRequest request = new B2CRequest();
        when(b2cService.getPaymentStatus("testID")).thenReturn(Optional.of(request));

        ResponseEntity<B2CRequest> response = b2cController.fetchPaymentStatus("testID");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void fetchPaymentStatus_NotFound() {
        when(b2cService.getPaymentStatus("testID")).thenReturn(Optional.empty());

        ResponseEntity<B2CRequest> response = b2cController.fetchPaymentStatus("testID");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updatePaymentStatus() {
        B2CResponse response = new B2CResponse();
        b2cController.updatePaymentStatus("testID", "testStatus");
        verify(b2cService, times(1)).updatePaymentStatus("testID", "testStatus");

    }
}
