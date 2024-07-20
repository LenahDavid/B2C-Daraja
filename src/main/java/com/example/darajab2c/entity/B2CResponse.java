package com.example.darajab2c.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "b2c_response")
@Data
public class B2CResponse {
    private String conversationID;
    private String originatorConversationID;
    private String responseCode;
    private String responseDescription;

    @DBRef
    private B2CResponse response;
}
