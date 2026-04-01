# AuthSentinel

Annotation-driven security framework for Spring Boot 3.x.

## Features

| Feature | Description |
|---|---|
| `@PublicEndpoint` | Marks a method/class as open — no auth required |
| `@SecuredEndpoint` | Requires authentication; optional role enforcement |
| `@RoleAllowed` | Restricts a method to one or more named roles |
| JWT (HS256/HS384/HS512) | HMAC-signed token generation and validation |
| Gateway trust mode | Trust pre-authenticated requests from an API gateway |
| X.509 certificate auth | Authenticate via client TLS certificates |
| BCrypt password support | Pre-configured `PasswordEncoder` bean |

## Installation

```xml
<dependency>
    <groupId>io.github.balamurali03</groupId>
    <artifactId>auth-sentinel-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Quick Start

### 1. Configure `application.yml`

```yaml
cosmo:
  security:
    jwt:
      secret: "replace-with-a-long-random-secret-at-least-32-chars"
      expiration: 3600000   # 1 hour in ms
      algorithm: HS256
```

### 2. Register the filter (Spring Security config)

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CosmoSecurityFilter cosmoSecurityFilter;

    public SecurityConfig(CosmoSecurityFilter cosmoSecurityFilter) {
        this.cosmoSecurityFilter = cosmoSecurityFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(cosmoSecurityFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
```

### 3. Annotate your controllers

```java
@RestController
public class ExampleController {

    @PublicEndpoint
    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @SecuredEndpoint
    @GetMapping("/profile")
    public String profile() {
        return "Hello, " + AuthContext.getAuthentication().getName();
    }

    @SecuredEndpoint(roles = "ROLE_ADMIN")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) { ... }

    @RoleAllowed({"ROLE_EDITOR", "ROLE_ADMIN"})
    @PostMapping("/articles")
    public Article createArticle(@RequestBody Article article) { ... }
}
```

## Generating a Token

```java
@Autowired CosmoTokenService tokenService;

String token = tokenService.generateToken("user@example.com");
// With issuer and custom claims:
String token = tokenService.generateToken(
        "user@example.com",
        "my-service",
        Map.of("roles", "ROLE_ADMIN")
);
```

## Deploy to Maven Central

```bash
# 1. Set up GPG key and add credentials to ~/.m2/settings.xml
# 2. Run:
mvn clean deploy -Prelease
```

See [DEPLOYMENT.md](DEPLOYMENT.md) for the full step-by-step guide.

## License

Apache License 2.0 — see [LICENSE](LICENSE).
