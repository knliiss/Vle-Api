package dev.knalis.vleapi.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final long DEFAULT_ACCESS_EXPIRATION = 1000L * 60 * 15;
    private final long DEFAULT_REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    @Value("${jwt.secret:defaultsecretchangeme}")
    private String secret;

    @Value("${jwt.use-rsa:false}")
    private boolean useRsa;

    @Value("${jwt.private-key:}")
    private String privateKeyPem;

    @Value("${jwt.public-key:}")
    private String publicKeyPem;

    private Key signingKey;
    private Key verificationKey;
    private SignatureAlgorithm algorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        try {
            if (useRsa) {
                algorithm = SignatureAlgorithm.RS256;
                if (privateKeyPem != null && !privateKeyPem.isBlank() && publicKeyPem != null && !publicKeyPem.isBlank()) {
                    PrivateKey priv = parsePrivateKeyFromPem(privateKeyPem);
                    PublicKey pub = parsePublicKeyFromPem(publicKeyPem);
                    signingKey = priv;
                    verificationKey = pub;
                } else {
                    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
                    kpg.initialize(2048);
                    KeyPair kp = kpg.generateKeyPair();
                    signingKey = kp.getPrivate();
                    verificationKey = kp.getPublic();
                }
            } else {
                signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                verificationKey = signingKey;
                algorithm = SignatureAlgorithm.HS256;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to initialize JWT keys", e);
        }
    }

    public String generateAccessToken(String username) {
        return generateToken(username, DEFAULT_ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, DEFAULT_REFRESH_EXPIRATION);
    }

    public String generateToken(String username, long ttlMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(signingKey, algorithm)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(verificationKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    private PrivateKey parsePrivateKeyFromPem(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String normalized = pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .trim();
        byte[] decoded = Base64.getDecoder().decode(normalized);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    private PublicKey parsePublicKeyFromPem(String pem) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String normalized = pem.replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)----", "")
                .replaceAll("\r\n", "")
                .replaceAll("\n", "")
                .trim();
        byte[] decoded = Base64.getDecoder().decode(normalized);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}