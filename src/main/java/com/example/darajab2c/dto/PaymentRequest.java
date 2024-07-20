package com.example.darajab2c.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long Amount;
    @JsonProperty("PartyA")
    private Long PartyA;
    @JsonProperty("PartyB")
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
