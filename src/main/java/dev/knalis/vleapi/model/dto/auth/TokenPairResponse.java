package dev.knalis.vleapi.model.dto.auth;

import lombok.Data;

@Data
public class TokenPairResponse {
    private String accessToken;
    private String refreshToken;

    public TokenPairResponse() {}

    public TokenPairResponse(String access, String refresh) {
        this.accessToken = access;
        this.refreshToken = refresh;
    }
}

