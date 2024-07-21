package com.example.darajab2c.service;

import okhttp3.*;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Base64;

@Service
public class MpesaGenerateToken {

    @Value("${daraja.auth-url}")
    private String authUrl;
    OkHttpClient client = new OkHttpClient.Builder().build();
    okhttp3.MediaType mediaType = okhttp3.MediaType.parse("text/plain");
    okhttp3.MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(JSON, "{}");
    Request request = new Request.Builder()
            .url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials")
            .get()
            .addHeader("Authorization", "Basic bm5Nd0hkdlRka21HbG1MWTI0NEtYNjk3S1pPSk5VUHJJMDhWeVpOSzBvbG1NeExrOnd3bzdFOUdlVFEzWllDMUVsOU9IVjB3c0packoyaGh1TXFrY1Z4V1pxeDMzdUo1dXAzZ2dDdTNBOFEzc3UwVXQ=")
            .addHeader("Cookie", "incap_ses_1353_2742146=P80TIk6pw1seyUwtXtLGEkqQNGYAAAAAbWoh6wdDVbx/84jwiFgTtA==; visid_incap_2742146=Cuc8d7s0RkWig+hfIC87OQbqLGYAAAAAQUIPAAAAAABydVO8KimWm744/kD+bKxR")
            .build();
    Response response;

    {
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getResponse() {
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

