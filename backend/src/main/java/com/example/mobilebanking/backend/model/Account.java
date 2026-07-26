package com.example.mobilebanking.backend.model;

public record Account(String maskId, String accountId, String accountName, String accountType, double balance) {
}
