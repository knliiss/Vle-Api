package dev.knalis.vleapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityHeadersFilter extends HttpFilter {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        byte[] nonceBytes = new byte[16];
        secureRandom.nextBytes(nonceBytes);
        String nonce = Base64.getEncoder().encodeToString(nonceBytes);
        req.setAttribute("cspNonce", nonce);

        res.setHeader("X-Content-Type-Options", "nosniff");

        res.setHeader("X-Frame-Options", "DENY");

        String csp = "default-src 'self'; script-src 'self' 'nonce-" + nonce + "'; style-src 'self' 'unsafe-inline'; img-src 'self' data:;";
        res.setHeader("Content-Security-Policy", csp);

        res.setHeader("Referrer-Policy", "no-referrer");

        res.setHeader("Permissions-Policy", "geolocation=(), microphone=()");

        res.setHeader("X-DNS-Prefetch-Control", "off");

        chain.doFilter(req, res);
    }
}
