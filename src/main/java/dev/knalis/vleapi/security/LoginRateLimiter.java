package dev.knalis.vleapi.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class LoginRateLimiter {

    private static class Entry {
        int attempts;
        Instant firstAttempt;
    }

    private final Map<String, Entry> map = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final long WINDOW_SECONDS = 60 * 5;

    public boolean isAllowed(String key) {
        Entry e = map.get(key);
        if (e == null) return true;
        if (Instant.now().isAfter(e.firstAttempt.plusSeconds(WINDOW_SECONDS))) {
            map.remove(key);
            return true;
        }
        return e.attempts < MAX_ATTEMPTS;
    }

    public void onFailure(String key) {
        map.compute(key, (k, v) -> {
            if (v == null) {
                Entry n = new Entry();
                n.attempts = 1;
                n.firstAttempt = Instant.now();
                return n;
            } else {
                v.attempts++;
                return v;
            }
        });
    }

    public void onSuccess(String key) {
        map.remove(key);
    }
}

