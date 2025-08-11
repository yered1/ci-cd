package com.secureapp.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OtelRequestFilter extends OncePerRequestFilter {
    private Tracer tracer;
    @PostConstruct public void init() { OpenTelemetry otel = GlobalOpenTelemetry.get(); this.tracer = otel.getTracer("secureapp"); }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Span span = tracer.spanBuilder("http.request").startSpan();
        try (Scope s = span.makeCurrent()) {
            span.setAttribute("http.target", request.getRequestURI());
            span.setAttribute("http.method", request.getMethod());
            chain.doFilter(request, response);
            span.setAttribute("http.status_code", response.getStatus());
        } finally { span.end(); }
    }
}
