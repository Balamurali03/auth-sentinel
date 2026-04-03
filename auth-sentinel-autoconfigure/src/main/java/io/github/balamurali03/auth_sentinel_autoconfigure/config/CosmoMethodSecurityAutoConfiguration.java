package io.github.balamurali03.auth_sentinel_autoconfigure.config;

import io.github.balamurali03.auth_sentinel_annotations.aop.CosmoSecurityAspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Auto-configuration that registers the {@link CosmoSecurityAspect} and enables
 * AspectJ auto-proxying.
 *
 * <p>Annotate your Spring Boot application with {@code @EnableAspectJAutoProxy}
 * only if you need to override proxy settings; this class handles it for
 * standard use cases.
 */
@Configuration
@EnableAspectJAutoProxy
public class CosmoMethodSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CosmoSecurityAspect cosmoSecurityAspect() {
        return new CosmoSecurityAspect();
    }
}
