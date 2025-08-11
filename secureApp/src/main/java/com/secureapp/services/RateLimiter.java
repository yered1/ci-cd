package com.secureapp.services;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiter {
    private final Map<String, Window> windows = new ConcurrentHashMap<>();
    private static class Window { AtomicInteger count = new AtomicInteger(0); long resetAt; }

    public boolean allow(String key, int limitPerMinute) {
        long now = Instant.now().getEpochSecond();
        Window w = windows.computeIfAbsent(key, k -> { Window nw = new Window(); nw.resetAt = now + 60; return nw; });
        if (now > w.resetAt) { w.count.set(0); w.resetAt = now + 60; }
        return w.count.incrementAndGet() <= limitPerMinute;
    }
}
