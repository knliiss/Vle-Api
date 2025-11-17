package dev.knalis.vleapi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    
    
    @Value("${logging.request.maxPayloadLength:2048}")
    private int maxPayloadLength;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - start;
            
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            Principal principal = request.getUserPrincipal();
            String principalName = principal == null ? "anonymous" : principal.getName();
            
            String requestBody = readRequestBody(wrappedRequest);
            String responseBody = readResponseBody(wrappedResponse);
            int status = wrappedResponse.getStatus();
            
            log.info("{} {}{} principal={} status={} duration={}ms reqSize={} respSize={} reqBody={}",
                     method,
                     uri,
                     (query == null || query.isEmpty()) ? "" : "?" + query,
                     principalName,
                     status,
                     duration,
                     (requestBody == null ? 0 : requestBody.length()),
                     (responseBody == null ? 0 : responseBody.length()),
                     truncate(requestBody));
            
            wrappedResponse.copyBodyToResponse();
        }
    }
    
    private String readRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf == null || buf.length == 0) return null;
        int length = Math.min(buf.length, maxPayloadLength);
        try {
            return new String(buf, 0, length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "[unknown]";
        }
    }
    
    private String readResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf == null || buf.length == 0) return null;
        int length = Math.min(buf.length, maxPayloadLength);
        try {
            return new String(buf, 0, length, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "[unknown]";
        }
    }
    
    private String truncate(String s) {
        if (s == null) return null;
        if (s.length() <= maxPayloadLength) return s;
        return s.substring(0, Math.max(0, maxPayloadLength - 12)) + "...(truncated)";
    }
}
