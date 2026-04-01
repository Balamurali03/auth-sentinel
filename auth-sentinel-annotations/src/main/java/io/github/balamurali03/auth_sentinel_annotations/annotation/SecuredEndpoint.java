package io.github.balamurali03.auth_sentinel_annotations.annotation;


import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecuredEndpoint {

    String[] roles() default {};

    boolean requireBearer() default true;

    boolean requireCertificate() default false;

    boolean requirePrincipal() default false;
}
