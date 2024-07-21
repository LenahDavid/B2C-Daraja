package com.example.darajab2c.repository;

import com.example.darajab2c.entity.B2CResponse;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface B2CResponseRepository extends MongoRepository<B2CResponse, String> {
    Optional<Object> findByOriginatorConversationID(String originatorConversationID);
}
