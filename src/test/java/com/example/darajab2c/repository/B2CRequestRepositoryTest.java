package com.example.darajab2c.repository;

import com.example.darajab2c.entity.B2CRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@DataMongoTest
public class B2CRequestRepositoryTest {

    @Autowired
    private B2CRequestRepository repository;

    @Test
    public void testSaveAndFindById() {
        B2CRequest request = new B2CRequest();
        request.setOriginatorConversationID("12345");
        request.setInitiatorName("testapi");
        request.setSecurityCredential("credential");
        request.setCommandID("BusinessPayment");
        request.setAmount(10L);
        request.setPartyA(Long.valueOf(Integer.valueOf("600988")));
        request.setPartyB(Long.valueOf("254708374149"));
        request.setRemarks("Test remarks");
        request.setQueueTimeOutURL("https://mydomain.com/b2c/queue");
        request.setResultURL("https://mydomain.com/b2c/result");
        request.setOccasion("null");
        request.setStatus("PENDING");

        B2CRequest savedRequest = repository.save(request);
        Optional<B2CRequest> foundRequest = repository.findById(savedRequest.getOriginatorConversationID());

        assertThat(foundRequest).isPresent();
        assertThat(foundRequest.get().getInitiatorName()).isEqualTo("testapi");
    }

}
