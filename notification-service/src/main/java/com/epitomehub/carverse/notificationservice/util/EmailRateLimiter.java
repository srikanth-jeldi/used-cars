package com.epitomehub.carverse.notificationservice.util;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmailRateLimiter {

    private final Map<String, Long> lastSent = new ConcurrentHashMap<>();
    private final long windowMillis = 5 * 60 * 1000; // 5 minutes

    public boolean allow(String key) {
        long now = Instant.now().toEpochMilli();
        Long prev = lastSent.get(key);

        if (prev == null || (now - prev) > windowMillis) {
            lastSent.put(key, now);
            return true;
        }
        return false;
    }
}
