package com.fiberplus.main.dtos.auth;

public class AuthResponseDto {
    private String token;

    public AuthResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
