package io.github.balamurali03.auth_sentinel_annotations.annotation;

import java.lang.annotation.*;

/**
 * Marks a method or class as requiring authentication.
 *
 * <ul>
 *   <li>{@code roles}              – one or more Spring Security role names (e.g. {@code "ROLE_ADMIN"}).
 *       If empty, any authenticated principal is accepted.</li>
 *   <li>{@code requireBearer}      – enforce that the request carries a Bearer JWT token.</li>
 *   <li>{@code requireCertificate} – enforce that the request carries an X.509 client certificate.</li>
 *   <li>{@code requirePrincipal}   – enforce that a non-anonymous principal is set.</li>
 * </ul>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredEndpoint {

    /** Required Spring Security role names. Empty means any authenticated user. */
    String[] roles() default {};

    /** Reject requests that do not carry a Bearer token. Default: true. */
    boolean requireBearer() default true;

    /** Reject requests that do not carry an X.509 client certificate. Default: false. */
    boolean requireCertificate() default false;

    /** Reject anonymous principals even when authentication is technically present. Default: false. */
    boolean requirePrincipal() default false;
}
