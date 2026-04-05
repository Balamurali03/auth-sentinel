package io.github.balamurali03.auth_sentinel_autoconfigure.config;


import io.github.balamurali03.auth_sentinel_autoconfigure.filter.CosmoSecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.balamurali03.auth_sentinel_autoconfigure.properties.CosmoSecurityWebProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(
    prefix = "cosmo.security",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableConfigurationProperties(CosmoSecurityWebProperties.class)  // ← ADD THIS
public class CosmoSecurityFilterConfiguration {

    private final CosmoSecurityFilter filter;
    private final CosmoSecurityWebProperties webProperties;  // ← ADD THIS

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain cosmoSecurityFilterChain(HttpSecurity http) throws Exception {

        if (!webProperties.isEnableCsrf())      http.csrf(AbstractHttpConfigurer::disable);
        if (!webProperties.isEnableFormLogin())  http.formLogin(AbstractHttpConfigurer::disable);
        if (!webProperties.isEnableHttpBasic())  http.httpBasic(AbstractHttpConfigurer::disable);
        if (!webProperties.isEnableLogout())     http.logout(AbstractHttpConfigurer::disable);

        http
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}