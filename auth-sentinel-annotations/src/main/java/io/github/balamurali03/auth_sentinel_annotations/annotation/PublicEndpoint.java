package io.github.balamurali03.auth_sentinel_annotations.annotation;

import java.lang.annotation.*;

/**
 * Marks a method or class as publicly accessible — no authentication required.
 * When placed on a class, all methods inherit this behaviour.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublicEndpoint {
}
