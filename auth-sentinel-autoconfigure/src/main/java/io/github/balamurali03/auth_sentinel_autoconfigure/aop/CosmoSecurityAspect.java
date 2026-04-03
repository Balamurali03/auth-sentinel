package io.github.balamurali03.auth_sentinel_autoconfigure.aop;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Order(1)
public class CosmoSecurityAspect {

    @Around("@within(publicEndpoint) || @annotation(publicEndpoint)")
    public Object handlePublic(ProceedingJoinPoint pjp,
                               PublicEndpoint publicEndpoint) throws Throwable {
        return pjp.proceed();
    }

    @Around("@within(securedEndpoint) || @annotation(securedEndpoint)")
    public Object handleSecured(ProceedingJoinPoint pjp,
                                SecuredEndpoint securedEndpoint) throws Throwable {

        if (!AuthContext.isAuthenticated()) {
            throw new CosmoAccessDeniedException("Authentication required");
        }

        if (securedEndpoint.requirePrincipal()) {
    if (AuthContext.getPrincipal() == null) {
        throw new CosmoAccessDeniedException("Principal required");
    }
}

        if (securedEndpoint.requireBearer()) {
            HttpServletRequest request = currentRequest();
            if (request == null || !hasBearerToken(request)) {
                throw new CosmoAccessDeniedException("Bearer token required");
            }
        }

        if (securedEndpoint.requireCertificate()) {
            HttpServletRequest request = currentRequest();
            if (request == null || !hasCertificate(request)) {
                throw new CosmoAccessDeniedException("Certificate required");
            }
        }

        if (securedEndpoint.roles().length > 0) {
            boolean granted = false;
            for (String role : securedEndpoint.roles()) {
                if (AuthContext.hasRole(role)) {
                    granted = true;
                    break;
                }
            }
            if (!granted) {
                throw new CosmoAccessDeniedException("Access denied");
            }
        }

        return pjp.proceed();
    }

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

        throw new CosmoAccessDeniedException("Access denied");
    }

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
        return request.getAttribute("jakarta.servlet.request.X509Certificate") != null;
    }
}