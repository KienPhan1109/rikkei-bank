package com.ptit.rikkei_bank.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_MDC_KEY = "traceId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            // Generate a unique trace ID for each request
            String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            MDC.put(TRACE_ID_MDC_KEY, traceId);
            
            chain.doFilter(request, response);
        } finally {
            // Must clear MDC after request completes to avoid memory leaks
            MDC.remove(TRACE_ID_MDC_KEY);
        }
    }
}
