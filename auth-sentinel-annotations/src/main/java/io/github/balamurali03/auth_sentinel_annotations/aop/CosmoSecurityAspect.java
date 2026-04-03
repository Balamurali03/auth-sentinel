package io.github.balamurali03.auth_sentinel_annotations.aop;

import io.github.balamurali03.auth_sentinel_annotations.annotation.PublicEndpoint;
import io.github.balamurali03.auth_sentinel_annotations.annotation.RoleAllowed;
import io.github.balamurali03.auth_sentinel_annotations.annotation.SecuredEndpoint;
import io.github.balamurali03.auth_sentinel_annotations.authorization.AuthContext;
import io.github.balamurali03.auth_sentinel_annotations.exception.CosmoAccessDeniedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP advice that enforces {@link PublicEndpoint}, {@link SecuredEndpoint}
 * and {@link RoleAllowed} annotations.
 *
 * <p>Evaluation order:
 * <ol>
 *   <li>If {@code @PublicEndpoint} is present the method proceeds immediately.</li>
 *   <li>If {@code @SecuredEndpoint} is present, authentication state,
 *       optional bearer / certificate requirements, and role membership
 *       are validated before proceeding.</li>
 *   <li>If {@code @RoleAllowed} is present, authentication state and role
 *       membership are validated.</li>
 * </ol>
 *
 * <p>Order is set to {@code 1} so this advice runs before most other aspects.
 */
@Aspect
@Component
@Order(1)
public class CosmoSecurityAspect {

    // ── @PublicEndpoint ─────────────────────────────────────────────────────

    /**
     * No checks — always proceeds.
     */
    @Around("@within(publicEndpoint) || @annotation(publicEndpoint)")
    public Object handlePublic(ProceedingJoinPoint pjp,
                               PublicEndpoint publicEndpoint) throws Throwable {
        return pjp.proceed();
    }

    // ── @SecuredEndpoint ────────────────────────────────────────────────────

    @Around("@within(securedEndpoint) || @annotation(securedEndpoint)")
    public Object handleSecured(ProceedingJoinPoint pjp,
                                SecuredEndpoint securedEndpoint) throws Throwable {

        // 1. Basic authentication check
        if (!AuthContext.isAuthenticated()) {
            throw new CosmoAccessDeniedException("Authentication required");
        }

        // 2. requirePrincipal — already implied by isAuthenticated(), but kept
        //    explicit for clarity and future override scenarios.
        if (securedEndpoint.requirePrincipal() && !AuthContext.isAuthenticated()) {
            throw new CosmoAccessDeniedException("A concrete principal is required");
        }

        // 3. requireBearer — request must carry an Authorization: Bearer header
        if (securedEndpoint.requireBearer()) {
            HttpServletRequest request = currentRequest();
            if (request == null || !hasBearerToken(request)) {
                throw new CosmoAccessDeniedException(
                        "This endpoint requires a Bearer token");
            }
        }

        // 4. requireCertificate — request must carry an X.509 client certificate
        if (securedEndpoint.requireCertificate()) {
            HttpServletRequest request = currentRequest();
            if (request == null || !hasCertificate(request)) {
                throw new CosmoAccessDeniedException(
                        "This endpoint requires a client X.509 certificate");
            }
        }

        // 5. Role check — if roles array is non-empty, at least one must match
        if (securedEndpoint.roles().length > 0) {
            boolean granted = false;
            for (String role : securedEndpoint.roles()) {
                if (AuthContext.hasRole(role)) {
                    granted = true;
                    break;
                }
            }
            if (!granted) {
                throw new CosmoAccessDeniedException(
                        "Access denied — required role not present");
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

        throw new CosmoAccessDeniedException(
                "Access denied — required role not present");
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private HttpServletRequest currentRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (attrs != null) ? attrs.getRequest() : null;
    }

    private boolean hasBearerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return header != null && header.startsWith("Bearer ");
    }

    private boolean hasCertificate(HttpServletRequest request) {
        Object certs = request.getAttribute(
                "jakarta.servlet.request.X509Certificate");
        return certs != null;
    }
}
