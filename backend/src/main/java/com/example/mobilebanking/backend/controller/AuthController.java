package com.example.mobilebanking.backend.controller;

import com.example.mobilebanking.backend.model.Account;
import com.example.mobilebanking.backend.model.LoginRequest;
import com.example.mobilebanking.backend.model.LoginResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        if ("demo@bank.com".equals(request.email()) && "password123".equals(request.password())) {
            return new LoginResponse(true, "demo-token", "Demo User");
        }
        return new LoginResponse(false, null, "Invalid credentials");
    }

    @GetMapping("/accounts")
    public List<Account> accounts() {
        return List.of(
                new Account("ACC-1001", "Primary Checking", "Checking", 5420.75),
                new Account("ACC-1002", "Savings Plus", "Savings", 12845.20),
                new Account("ACC-1003", "Travel Card", "Credit", 1840.00)
        );
    }
}
