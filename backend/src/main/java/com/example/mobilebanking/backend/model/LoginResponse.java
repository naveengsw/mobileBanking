package com.example.mobilebanking.backend.model;

public record LoginResponse(boolean success, String token, String message) {
}
