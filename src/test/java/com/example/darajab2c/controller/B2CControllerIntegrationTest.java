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
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class B2CControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private B2CRequestRepository b2cRequestRepository;

    @Test
    public void testReceiveB2CRequest() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOriginatorConversationID("testID");
        // Set other fields as required

        mockMvc.perform(post("/api/b2c/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originatorConversationID\":\"testID\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Success")));
    }

    @Test
    public void testGetAllRequests() throws Exception {
        mockMvc.perform(get("/api/b2c/status"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFetchPaymentStatus() throws Exception {
        B2CRequest b2cRequest = new B2CRequest();
        b2cRequest.setOriginatorConversationID("testID");
        b2cRequestRepository.save(b2cRequest);

        MvcResult result = mockMvc.perform(get("/api/b2c/status/testID"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(content.contains("testID"), true);
    }

    @Test
    public void testUpdatePaymentStatus() throws Exception {
        B2CRequest b2cRequest = new B2CRequest();
        b2cRequest.setOriginatorConversationID("testID");
        b2cRequestRepository.save(b2cRequest);

        mockMvc.perform(put("/api/b2c/status/testID")
                        .param("status", "Completed"))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment status updated successfully"));

        Optional<B2CRequest> updatedRequest = b2cRequestRepository.findById(b2cRequest.getOriginatorConversationID());
        assertEquals("Completed", updatedRequest.get().getStatus());
    }
}