package io.github.balamurali03.auth_sentinel_autoconfigure.config;

import io.github.balamurali03.auth_sentinel_annotations.exception.CosmoAccessDeniedException;
import io.github.balamurali03.auth_sentinel_core.exception.CosmoSecurityException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Global {@link RestControllerAdvice} that translates AuthSentinel security
 * exceptions into structured JSON error responses.
 *
 * <p>Only activated when Spring MVC's {@link DispatcherServlet} is on the
 * classpath and no custom handler is already defined. Consumer applications
 * can override this by declaring their own {@code @RestControllerAdvice} bean.
 *
 * <h3>Response shapes</h3>
 * <pre>
 * HTTP 401 — CosmoSecurityException (invalid / expired token)
 * {
 *   "status": 401,
 *   "error": "Unauthorized",
 *   "message": "...",
 *   "timestamp": "2024-01-01T00:00:00Z"
 * }
 *
 * HTTP 403 — CosmoAccessDeniedException (missing role, missing bearer, etc.)
 * {
 *   "status": 403,
 *   "error": "Forbidden",
 *   "message": "...",
 *   "timestamp": "2024-01-01T00:00:00Z"
 * }
 * </pre>
 */
@RestControllerAdvice
@Configuration
@ConditionalOnClass(DispatcherServlet.class)
@ConditionalOnMissingBean(CosmoSecurityExceptionHandler.class)
public class CosmoSecurityExceptionHandler {

    @ExceptionHandler(CosmoSecurityException.class)
    public ResponseEntity<Map<String, Object>> handleCosmoSecurityException(
            CosmoSecurityException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(CosmoAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            CosmoAccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status,
                                                               String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status",    status.value());
        body.put("error",     status.getReasonPhrase());
        body.put("message",   message);
        body.put("timestamp", Instant.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}
