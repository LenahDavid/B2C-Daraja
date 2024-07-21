package com.example.darajab2c.repository;

import com.example.darajab2c.entity.B2CRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface B2CRequestRepository extends MongoRepository<B2CRequest, String> {
    Optional<B2CRequest> findByOriginatorConversationID(String originatorConversationID);

    @Query("{ 'originatorConversationID': ?0 }")
    @Update("{ '$set': { 'status': ?1 } }")
    void updateStatusByOriginatorConversationID(String originatorConversationID, String status);
}