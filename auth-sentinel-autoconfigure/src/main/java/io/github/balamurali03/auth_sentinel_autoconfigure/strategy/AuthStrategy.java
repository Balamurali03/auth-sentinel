package io.github.balamurali03.auth_sentinel_autoconfigure.strategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface AuthStrategy {

    boolean supports(HttpServletRequest request);

    Authentication authenticate(HttpServletRequest request);
}
