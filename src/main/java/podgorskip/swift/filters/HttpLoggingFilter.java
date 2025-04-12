package podgorskip.swift.filters;

import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class HttpLoggingFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        Instant start = Instant.now();

        log.info("Incoming request: {} {}", method, uri);

        try {
            filterChain.doFilter(request, response);
        } finally {
            Instant end = Instant.now();
            long duration = Duration.between(start, end).toMillis();
            int status = response.getStatus();

            log.info("Response: {} '{}' - status: {} - time: {} ms", method, uri, status, duration);

            meterRegistry.timer("http.requests",
                            "method", method,
                            "uri", uri,
                            "status", String.valueOf(status))
                    .record(Duration.ofMillis(duration));
        }
    }
}
