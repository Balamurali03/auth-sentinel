package io.github.balamurali03.auth_sentinel_annotations.annotation;

import java.lang.annotation.*;

/**
 * Restricts a method to callers holding at least one of the listed roles.
 * Role names must follow Spring Security conventions, e.g. {@code "ROLE_ADMIN"}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleAllowed {

    /** One or more required role names. */
    String[] value();
}
