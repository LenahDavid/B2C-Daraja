package com.example.darajab2c.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "b2c_requests")
@Data
public class B2CRequest {
    private String originatorConversationID;
    private String InitiatorName;
    private String SecurityCredential;
    private String CommandID;
    private Long Amount;
    private Long PartyA;
    private Long PartyB;
    private String Remarks;
    private String QueueTimeOutURL;
    private String ResultURL;
    private String Occasion;
    private LocalDateTime timestamp;
    private String status;


}
