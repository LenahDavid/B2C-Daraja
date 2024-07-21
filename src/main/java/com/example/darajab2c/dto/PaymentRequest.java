package com.example.darajab2c.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PaymentRequest implements Serializable {
    @JsonProperty("OriginatorConversationID")
    private String OriginatorConversationID;
    @JsonProperty("InitiatorName")
    private String InitiatorName;
    @JsonProperty("SecurityCredential")
    private String SecurityCredential;
    @JsonProperty("CommandID")
    private String CommandID;
    @JsonProperty("Amount")
    @NotNull(message = "Amount is required")
    @Min(value = 10, message = "Amount should be at least 10")
    @Max(value = 150, message = "Amount should be at most 150000")
    private Long Amount;
    @JsonProperty("PartyA")
    private Long PartyA;
    @JsonProperty("PartyB")
    @NotNull(message = "Mobile number is required")
    private Long PartyB;
    @JsonProperty("Remarks")
    private String Remarks;
    @JsonProperty("QueueTimeOutURL")
    private String QueueTimeOutURL;
    @JsonProperty("ResultURL")
    private String ResultURL;
    @JsonProperty("Occasion")
    private String occasion;
    private LocalDateTime timestamp;
    private String status;

}
