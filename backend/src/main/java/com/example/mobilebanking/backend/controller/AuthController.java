package com.example.mobilebanking.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mobilebanking.backend.model.Account;
import com.example.mobilebanking.backend.model.LoginRequest;
import com.example.mobilebanking.backend.model.LoginResponse;
import com.example.mobilebanking.backend.utils.CryptoUtils;

@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if ("demo@bank.com".equals(request.email()) && "password123".equals(CryptoUtils.decryptPassword(request.password()))) {
            return new LoginResponse(true, UUID.randomUUID().toString(), "Demo User");
        }
        return new LoginResponse(false, null, "Invalid credentials");
    }

    @GetMapping("/accounts")
    public List<Account> accounts() {
        return List.of(
                new Account( "d89234n-3mr04", "****-1001", "Primary Checking", "Checking", 5420.75),
                new Account("ckds038hdS3-d3r", "****-1002", "Savings Plus", "Savings", 12845.20),
                new Account("09di-UHkslpm234", "****-1003", "Travel Card", "Credit", 1840.00)
        );
    }
}
