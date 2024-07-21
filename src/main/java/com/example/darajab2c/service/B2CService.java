package com.example.darajab2c.service;

import com.example.darajab2c.controllers.B2CController;
import com.example.darajab2c.controllers.GenerateToken;
import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.entity.PaymentStatus;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.repository.B2CResponseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class B2CService {

    @Autowired
    private B2CRequestRepository b2cRequestRepository;

    @Autowired
    private B2CResponseRepository b2cResponseRepository;

    @Autowired
    private KafkaTemplate<String, B2CRequest> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MpesaGenerateToken authService;

    @Value("${daraja.b2c-url}")
    private String darajaApiUrl;
    private static final Logger logger = LoggerFactory.getLogger(B2CService.class);
    public String initiateB2CPayment(String originatorConversationID, String initiatorName, String securityCredential, String commandID, Long amount, Long partyA, Long partyB, String remarks, String queueTimeOutURL, String resultURL, String occasion) {
        GenerateToken generateToken = new GenerateToken();
        String generatedToken = generateToken.generateToken();
        JSONObject tokenJson = new JSONObject(generatedToken);
        String accessToken = tokenJson.getString("access_token");
        String authorization = "Bearer " + accessToken;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        okhttp3.MediaType mediaType = MediaType.parse("application/json");

        String json = String.format("{\"InitiatorName\": \"%s\",\"SecurityCredential\":\"%s\",\"CommandID\": \"%s\",\"Amount\": \"%s\", \"PartyA\": \"%s\", \"PartyB\": \"%s\",\"Remarks\": \"%s\", \"QueueTimeOutURL\": \"%s\",\"ResultURL\": \"%s\",\"Occasion\": \"%s\"}",
                initiatorName, securityCredential, commandID, amount, partyA, partyB, remarks, queueTimeOutURL, resultURL, occasion);

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(darajaApiUrl)
                .method("POST", body)
                .addHeader("Authorization", authorization)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            System.out.println("Request URL: " + request.url());
            System.out.println("Request Headers: " + request.headers());
            System.out.println("Request Body: " + json);
            System.out.println("Response Code: " + response.code());
            System.out.println("Response Body: " + responseBody);

            // Save the request
            B2CRequest b2cRequest = new B2CRequest();
            b2cRequest.setOriginatorConversationID(originatorConversationID);
            b2cRequest.setInitiatorName(initiatorName);
            b2cRequest.setSecurityCredential(securityCredential);
            b2cRequest.setCommandID(commandID);
            b2cRequest.setAmount(amount);
            b2cRequest.setPartyA(partyA);
            b2cRequest.setPartyB(partyB);
            b2cRequest.setRemarks(remarks);
            b2cRequest.setQueueTimeOutURL(queueTimeOutURL);
            b2cRequest.setResultURL(resultURL);
            b2cRequest.setOccasion(occasion);
            b2cRequest.setTimestamp(LocalDateTime.now());
            b2cRequest.setStatus("Pending");
            b2cRequestRepository.save(b2cRequest);

            // Save the response
            if (response.isSuccessful()) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                B2CResponse b2cResponse = new B2CResponse();
                b2cResponse.setConversationID(jsonResponse.getString("ConversationID"));
                b2cResponse.setOriginatorConversationID(jsonResponse.getString("OriginatorConversationID"));
                b2cResponse.setResponseCode(jsonResponse.getString("ResponseCode"));
                b2cResponse.setResponseDescription(jsonResponse.getString("ResponseDescription"));
                b2cResponseRepository.save(b2cResponse);
                return responseBody;
            } else {
                return "Error: " + response.code() + " " + response.message();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    public void sendB2CRequestToKafka(B2CRequest request) {
        logger.info("Sending B2C request to Kafka: {}", request.getOriginatorConversationID());
        kafkaTemplate.send("b2c-requests", request.getOriginatorConversationID(), request);
    }
    public B2CResponse processB2CRequest(B2CRequest request) {
        sendB2CRequestToKafka(request);

        // Call Daraja API and handle saving to the database
        String responseString = initiateB2CPayment(
                request.getOriginatorConversationID(),
                request.getInitiatorName(),
                request.getSecurityCredential(),
                request.getCommandID(),
                request.getAmount(),
                request.getPartyA(),
                request.getPartyB(),
                request.getRemarks(),
                request.getQueueTimeOutURL(),
                request.getResultURL(),
                request.getOccasion()
        );

        // Parse and return the response
        JSONObject responseJson = new JSONObject(responseString);
        B2CResponse response = new B2CResponse();
        response.setConversationID(responseJson.optString("ConversationID"));
        response.setOriginatorConversationID(request.getOriginatorConversationID());
        response.setResponseCode(responseJson.getString("ResponseCode"));
        response.setResponseDescription(responseJson.getString("ResponseDescription"));
        return response;
    }
    public Optional<B2CRequest> getPaymentStatus(String originatorConversationID) {
        return b2cRequestRepository.findByOriginatorConversationID(originatorConversationID);
    }

    public void updatePaymentStatus(String originatorConversationID, String status) {
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            b2cRequestRepository.updateStatusByOriginatorConversationID(originatorConversationID, paymentStatus.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid payment status: " + status);
        }
    }


    }
