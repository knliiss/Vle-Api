package dev.knalis.vleapi.model.dto.auth;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String error;

    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }
    
}

