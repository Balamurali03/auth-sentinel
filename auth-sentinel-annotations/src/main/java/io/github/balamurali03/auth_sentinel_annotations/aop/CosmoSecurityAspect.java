package io.github.balamurali03.auth_sentinel_annotations.aop;



import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import io.github.balamurali03.auth_sentinel_annotations.annotation.PublicEndpoint;
import io.github.balamurali03.auth_sentinel_annotations.annotation.RoleAllowed;
import io.github.balamurali03.auth_sentinel_annotations.annotation.SecuredEndpoint;
import io.github.balamurali03.auth_sentinel_annotations.authorization.AuthContext;
import io.github.balamurali03.auth_sentinel_annotations.exception.CosmoAccessDeniedException;

@Aspect
@Component
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

        if (securedEndpoint.roles().length > 0) {
            for (String role : securedEndpoint.roles()) {
                if (AuthContext.hasRole(role)) {
                    return pjp.proceed();
                }
            }
            throw new CosmoAccessDeniedException("Required role not present");
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

        throw new CosmoAccessDeniedException("Access denied. Role not allowed.");
    }
}
