package io.github.balamurali03.auth_sentinel_annotations.aop;

import io.github.balamurali03.auth_sentinel_annotations.annotation.PublicEndpoint;
import io.github.balamurali03.auth_sentinel_annotations.annotation.RoleAllowed;
import io.github.balamurali03.auth_sentinel_annotations.annotation.SecuredEndpoint;
import io.github.balamurali03.auth_sentinel_annotations.authorization.AuthContext;
import io.github.balamurali03.auth_sentinel_annotations.exception.CosmoAccessDeniedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP advice that enforces {@link PublicEndpoint}, {@link SecuredEndpoint}
 * and {@link RoleAllowed} annotations.
 *
 * <p>Order is set to {@code 1} so this advice runs before most other aspects.
 */
@Aspect
@Component
@Order(1)
public class CosmoSecurityAspect {

    // ── @PublicEndpoint ─────────────────────────────────────────────────────

    @Around("@within(publicEndpoint) || @annotation(publicEndpoint)")
    public Object handlePublic(ProceedingJoinPoint pjp,
                               PublicEndpoint publicEndpoint) throws Throwable {
        // No checks – always proceed.
        return pjp.proceed();
    }

    // ── @SecuredEndpoint ────────────────────────────────────────────────────

    @Around("@within(securedEndpoint) || @annotation(securedEndpoint)")
    public Object handleSecured(ProceedingJoinPoint pjp,
                                SecuredEndpoint securedEndpoint) throws Throwable {

        if (!AuthContext.isAuthenticated()) {
            throw new CosmoAccessDeniedException("Authentication required");
        }

        // requirePrincipal is already satisfied by isAuthenticated() above,
        // but we keep the explicit check for documentation clarity.
        if (securedEndpoint.requirePrincipal() && !AuthContext.isAuthenticated()) {
            throw new CosmoAccessDeniedException("A concrete principal is required");
        }

        // Role check – if roles array is non-empty, at least one must match.
        if (securedEndpoint.roles().length > 0) {
            boolean granted = false;
            for (String role : securedEndpoint.roles()) {
                if (AuthContext.hasRole(role)) {
                    granted = true;
                    break;
                }
            }
            if (!granted) {
                throw new CosmoAccessDeniedException("Required role not present");
            }
        }

        return pjp.proceed();
    }

    // ── @RoleAllowed ────────────────────────────────────────────────────────

    @Around("@annotation(roleAllowed)")
    public Object handleRoleAllowed(ProceedingJoinPoint pjp,
                                    RoleAllowed roleAllowed) throws Throwable {

        if (!AuthContext.isAuthenticated()) {
            throw new CosmoAccessDeniedException("Authentication required");
        }

        for (String role : roleAllowed.value()) {
            if (AuthContext.hasRole(role)) {
                return pjp.proceed();
            }
        }

        throw new CosmoAccessDeniedException("Access denied – required role not present");
    }
}
