package com.example.darajab2c.controllers;

import com.example.darajab2c.dto.PaymentRequest;
import com.example.darajab2c.entity.B2CRequest;
import com.example.darajab2c.entity.B2CResponse;
import com.example.darajab2c.repository.B2CRequestRepository;
import com.example.darajab2c.service.B2CService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.List;
import java.util.Optional;

@ApiResponses({@ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "403", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Not Found"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "201", description = "Created")
})
@RestController
@RequestMapping("/api/b2c")
public class B2CController {

    @Autowired
    private B2CService b2cService;
    @Autowired
    B2CRequestRepository b2cRequestRepository;
    private static final Logger logger = LoggerFactory.getLogger(B2CController.class);
    @Operation(
            description = "Posting B2C Request",
            summary = "Posting the request"

    )

    @PostMapping("/request")
    public ResponseEntity<String> receiveB2CRequest(@RequestBody PaymentRequest paymentRequest) {
        try {
//            if (!isValidAmount(paymentRequest.getAmount()) || !isValidKenyanMobileNumber(String.valueOf(paymentRequest.getPartyB()))) {
//                if (!isValidAmount(paymentRequest.getAmount())) {
//                    return ResponseEntity.badRequest().body("Invalid amount. Please provide a valid amount.");
//                } else {
//                    return ResponseEntity.badRequest().body("Invalid mobile number. Please provide a valid Kenyan Safaricom mobile number.");
//                }
//            }

            // Validate amount
            if (!isValidAmount(paymentRequest.getAmount())) {
                return ResponseEntity.badRequest().body("Invalid amount. Please provide an amount between KSh 10 and K KSh 150,000.");
            }

            String response = b2cService.initiateB2CPayment(
                    paymentRequest.getOriginatorConversationID(),
                    paymentRequest.getInitiatorName(),
                    paymentRequest.getSecurityCredential(),
                    paymentRequest.getCommandID(),
                    paymentRequest.getAmount(),
                    paymentRequest.getPartyA(),
                    paymentRequest.getPartyB(),
                    paymentRequest.getRemarks(),
                    paymentRequest.getQueueTimeOutURL(),
                    paymentRequest.getResultURL(),
                    paymentRequest.getOccasion()
            );
            logger.info("B2C simulate response: {}", response);
            return ResponseEntity.ok(response);
        } catch (HttpStatusCodeException e) {
            String responseBody = e.getResponseBodyAsString();
            logger.error("Error simulating B2C: {}", responseBody, e);
            return ResponseEntity.status(e.getStatusCode())
                    .body("Error simulating B2C: " + responseBody);
        }
    }
    private boolean isValidKenyanMobileNumber(String mobileNumber) {
        mobileNumber = mobileNumber.replace("+", "");

        return mobileNumber != null && mobileNumber.matches("^254\\d{10}$");
    }
    private boolean isValidAmount(Long amount) {
        return amount != null && amount >= 10 && amount <= 150000;
    }
    @GetMapping("/status")
    public List<B2CRequest> getAllRequests() {
        return b2cRequestRepository.findAll();
    }
    @Operation(
            description = "Gettingthe status of the request",
            summary = "Fetching the request"

    )
    @GetMapping("/status/{originatorConversationID}")
    public ResponseEntity<B2CRequest> fetchPaymentStatus(@PathVariable String originatorConversationID) {
        Optional<B2CRequest> requestOptional = b2cService.getPaymentStatus(originatorConversationID);

        return requestOptional
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new B2CRequest()));
    }
    @Operation(
            description = "Updating B2C Request",
            summary = "Uodating the request"

    )


    @PutMapping("/status/{originatorConversationID}")
    public ResponseEntity<String> updatePaymentStatus(
            @PathVariable String originatorConversationID,
            @RequestParam String status) {
        try {
            b2cService.updatePaymentStatus(originatorConversationID, status);
            return ResponseEntity.ok("Payment status updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating payment status: " + e.getMessage());
        }
    }


}
