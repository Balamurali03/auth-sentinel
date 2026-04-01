package io.github.balamurali03.auth_sentinel_annotations.annotation;


import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RoleAllowed {

    String[] value();
}
