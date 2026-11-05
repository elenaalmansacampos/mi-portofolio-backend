package com.elena.portafolio.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_TRACKED_CLIENTS = 10_000;

    private final int maxRequests;
    private final long windowMillis;

    private final Map<String, Deque<Long>> requests =
            new ConcurrentHashMap<>();


    public RateLimitFilter(
            @Value("${rate-limit.max-requests:10}") int maxRequests,
            @Value("${rate-limit.window-seconds:60}") long windowSeconds
    ) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowSeconds * 1000;
    }


    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        boolean limitedPath = path.equals("/api/auth/login")
                || path.equals("/api/contact");

        return !("POST".equals(request.getMethod()) && limitedPath);
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long now = System.currentTimeMillis();
        String key = clientIp(request) + ":" + request.getServletPath();

        Deque<Long> timestamps =
                requests.computeIfAbsent(key, ignored -> new ArrayDeque<>());

        synchronized (timestamps) {

            while (!timestamps.isEmpty()
                    && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }

            if (timestamps.size() >= maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setCharacterEncoding("UTF-8");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setHeader(
                        "Retry-After",
                        String.valueOf(windowMillis / 1000)
                );
                response.getWriter().write(
                        "{\"error\":\"Demasiadas solicitudes. Inténtalo más tarde.\"}"
                );
                return;
            }

            timestamps.addLast(now);
        }

        if (requests.size() > MAX_TRACKED_CLIENTS) {
            cleanup(now);
        }

        filterChain.doFilter(request, response);
    }


    private String clientIp(HttpServletRequest request) {

        String forwarded = request.getHeader("X-Forwarded-For");

        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }


    private void cleanup(long now) {

        requests.entrySet().removeIf(entry -> {

            Deque<Long> timestamps = entry.getValue();

            synchronized (timestamps) {
                return timestamps.isEmpty()
                        || now - timestamps.peekLast() > windowMillis;
            }
        });
    }
}
