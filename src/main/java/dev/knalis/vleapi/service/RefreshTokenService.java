package dev.knalis.vleapi.service;

import dev.knalis.vleapi.model.entity.RefreshToken;
import dev.knalis.vleapi.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    public RefreshToken create(String username, String token, Instant expiresAt) {
        RefreshToken rt = new RefreshToken();
        rt.setToken(token);
        rt.setUsername(username);
        rt.setCreatedAt(Instant.now());
        rt.setExpiresAt(expiresAt);
        rt.setRevoked(false);
        return refreshTokenRepo.save(rt);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepo.save(token);
    }
}

