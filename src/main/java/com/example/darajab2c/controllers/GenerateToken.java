package com.example.darajab2c.controllers;

import com.example.darajab2c.service.MpesaGenerateToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mpesa")
public class GenerateToken {


    @GetMapping("/generateToken")
    public String generateToken() {
        MpesaGenerateToken mpesaGenerateToken = new MpesaGenerateToken();
        return mpesaGenerateToken.getResponse();
    }
}