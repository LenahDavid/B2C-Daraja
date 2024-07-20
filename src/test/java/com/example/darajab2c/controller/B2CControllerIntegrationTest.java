package com.example.darajab2c.controller;
import com.example.darajab2c.dto.PaymentRequest;
import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class B2CControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private B2CRequestRepository b2cRequestRepository;

    @BeforeEach
    void setUp() {
        b2cRequestRepository.deleteAll();
    }

    @Test
    void receiveB2CRequest() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOriginatorConversationID("testID");
        paymentRequest.setInitiatorName("testName");
        // Set other fields...

        mockMvc.perform(post("/api/b2c/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests() throws Exception {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("testID");
        b2cRequestRepository.save(request);

        mockMvc.perform(get("/api/b2c/status/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].originatorConversationID").value("testID"));
    }

    @Test
    void fetchPaymentStatus() throws Exception {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("testID");
        b2cRequestRepository.save(request);

        mockMvc.perform(get("/api/b2c/status/testID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originatorConversationID").value("testID"));
    }

    @Test
    void updatePaymentStatus() throws Exception {
        B2CResponse response = new B2CResponse();
        response.setOriginatorConversationID("testID");

        mockMvc.perform(put("/api/b2c/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(response)))
                .andExpect(status().isOk());
    }
}