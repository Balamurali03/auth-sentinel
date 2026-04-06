# 🛡️ AuthSentinel

[![Build Status](https://github.com/Balamurali03/auth-sentinel/actions/workflows/maven.yml/badge.svg)](https://github.com/Balamurali03/auth-sentinel/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.balamurali03/auth-sentinel-starter.svg)](https://central.sonatype.com/artifact/io.github.balamurali03/auth-sentinel-starter)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

---

## ⚡ What is AuthSentinel?

**AuthSentinel** is a **plug-and-play security framework for Spring Boot** that eliminates boilerplate and lets you secure APIs using **simple annotations**.

> Add dependency → Configure → Annotate → Done ✅

No `SecurityFilterChain`, no complex config, no repeated JWT handling.

---

## 🚀 Why AuthSentinel?

Spring Security is powerful… but:

* ❌ Too much boilerplate
* ❌ Repeated JWT logic across services
* ❌ Complex configuration for simple use-cases

**AuthSentinel solves this by:**

* ⚡ Zero-config auto security
* 🧩 Annotation-driven access control
* 🔐 Built-in JWT + multi-auth strategies
* 🔄 Extensible architecture (strategy pattern)

---

## 🔥 Key Features

* 🧩 **Annotation-driven security**

  * `@PublicEndpoint`
  * `@SecuredEndpoint`
  * `@RoleAllowed`

* 🔐 **Multiple authentication strategies**

  * JWT (HS256/384/512)
  * API Gateway Trust Mode
  * X.509 Client Certificates (mTLS)

* ⚙️ **Auto Configuration**

  * No manual security setup required
  * Fully Spring Boot integrated

* 🧠 **Strategy Pattern Architecture**

  * Plug-and-play authentication mechanisms

* 🛡️ **Production-ready defaults**

  * BCrypt password encoding
  * Structured 401 / 403 responses
  * Secure filter chain

---

## ⚡ Quick Start (30 seconds)

### 1. Add Dependency

```xml
<dependency>
    <groupId>io.github.balamurali03</groupId>
    <artifactId>auth-sentinel-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

### 2. Configure JWT

```yaml
cosmo:
  security:
    jwt:
      secret: your-secure-secret-key-32-plus-chars
      expiration: 3600000
```

---

### 3. Secure APIs with Annotations

```java
@RestController
@RequestMapping("/api")
public class UserController {

    @PublicEndpoint
    @PostMapping("/register")
    public User register(@RequestBody User user) { ... }

    @SecuredEndpoint
    @GetMapping("/profile")
    public UserDto getProfile() { ... }

    @SecuredEndpoint(roles = "ADMIN")
    @DeleteMapping("/admin/users/{id}")
    public void deleteUser(@PathVariable String id) { ... }

    @RoleAllowed({"ADMIN", "MANAGER"})
    @PostMapping("/reports")
    public Report createReport(@RequestBody ReportRequest req) { ... }
}
```

---

## 🧠 How It Works

```
Request
  ↓
CosmoSecurityFilter
  ↓
AuthStrategyResolver
  ├── JWT Strategy
  ├── Gateway Strategy
  └── Certificate Strategy
  ↓
SecurityContext
  ↓
Controller
  ↓
AOP (Annotation Enforcement)
```

---

## 🏗️ Architecture

```
auth-sentinel
├── annotations        → Custom security annotations + AOP
├── core               → JWT engine, token handling
├── autoconfigure      → Spring Boot integration
├── starter            → Consumer dependency
```

✔ Clean separation
✔ Framework-agnostic core
✔ Boot-optimized integration

---

## 🔐 Authentication Strategies

| Strategy     | Description                                  |
| ------------ | -------------------------------------------- |
| JWT          | Standard bearer token authentication         |
| Gateway Mode | Trusted internal headers (`X-Internal-Call`) |
| X.509        | Mutual TLS authentication                    |

---

## 🎯 Example Use Case

👉 Microservices with API Gateway

* Gateway validates JWT
* Passes user info via headers
* Internal services trust gateway

**Result:**

* No JWT duplication
* No secret sharing
* Faster services

---

## ⚙️ Customisation

AuthSentinel is **fully extensible**:

```java
@Bean
public AuthStrategy apiKeyStrategy() {
    return new ApiKeyAuthStrategy();
}
```

```java
@Bean
public CosmoTokenService customTokenService() {
    return new MyCustomTokenService();
}
```

✔ All components are overrideable
✔ Uses `@ConditionalOnMissingBean`

---

## 🧪 Error Handling

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "timestamp": "2026-01-01T10:00:00Z"
}
```

---

## 📦 Installation

```bash
git clone https://github.com/Balamurali03/auth-sentinel.git
cd auth-sentinel
./mvnw clean install
```

---

## 🚀 Publishing

Release with:

```bash
git tag v1.0.1
git push origin v1.0.1
```

CI will:

* Build
* Sign artifacts
* Publish to Maven Central

---

## 🤝 Contributing

PRs are welcome!
Open an issue before major changes.

---

## 📜 License

Licensed under Apache 2.0.

---

## 💡 Vision

AuthSentinel aims to become:

> “The easiest way to secure Spring Boot applications without sacrificing flexibility.”

---

## 👨‍💻 Author

**Balamurali R (CosmoCoder)**
Building developer-first tools 🚀


<!-- # AuthSentinel

[![Build Status](https://github.com/Balamurali03/auth-sentinel/actions/workflows/maven.yml/badge.svg)](https://github.com/Balamurali03/auth-sentinel/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.balamurali03/auth-sentinel-starter.svg)](https://central.sonatype.com/artifact/io.github.balamurali03/auth-sentinel-starter)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

**AuthSentinel** is a plug-and-play, annotation-driven security framework for Spring Boot 3.x. Drop in the starter, add your properties, annotate your controllers — done. No `SecurityFilterChain` beans, no `WebSecurityConfigurerAdapter`, no token-handling boilerplate.

---

## Table of Contents

- [Features](#features)
- [Module Structure](#module-structure)
- [Quick Start](#quick-start)
- [Configuration Reference](#configuration-reference)
- [Annotations](#annotations)
- [Role-Based Access](#role-based-access)
- [Authentication Strategies](#authentication-strategies)
- [How It Works](#how-it-works)
- [Advanced Usage](#advanced-usage)
- [Customisation](#customisation)
- [Building from Source](#building-from-source)
- [Publishing to Maven Central](#publishing-to-maven-central)
- [Contributing](#contributing)
- [License](#license)

---

## Features

| Feature | Details |
|---|---|
| `@PublicEndpoint` | Bypasses all security checks |
| `@SecuredEndpoint` | Requires authentication, optional roles, bearer, or certificate enforcement |
| `@RoleAllowed` | Fine-grained role check on any method |
| JWT (HS256/384/512) | Token generation, validation, subject, claims & roles extraction |
| API Gateway trust mode | Pre-auth via `X-Internal-Call` / `X-User-*` headers |
| X.509 client certificates | Mutual TLS authentication |
| Strategy pattern | Pluggable, ordered authentication chain per request |
| BCrypt passwords | Auto-configured `PasswordEncoder` bean |
| Global error handler | Structured JSON 401 / 403 responses |
| Spring Boot auto-config | Zero XML, zero `@Configuration` needed in consumer apps |
| Web security toggles | Opt-in form login, HTTP basic, CSRF, logout via `application.yml` |

---

## Module Structure

```
auth-sentinel/
├── auth-sentinel-annotations/   # Pure Java annotations + AOP aspect (no Spring Boot dep)
├── auth-sentinel-core/          # JWT engine, token service, exceptions (no Spring Boot dep)
├── auth-sentinel-autoconfigure/ # Spring Boot wiring: properties, filter, strategies, handler
├── auth-sentinel-starter/       # Thin BOM-style starter (what consumers depend on)
└── pom.xml                      # Parent POM
```

The **core** and **annotations** modules have **no Spring Boot dependency** — they are plain Spring Framework. This makes them usable in non-Boot environments and keeps the architecture clean.

---

## Quick Start

### 1. Add the starter

```xml
<dependency>
    <groupId>io.github.balamurali03</groupId>
    <artifactId>auth-sentinel-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configure `application.yml`

```yaml
cosmo:
  security:
    jwt:
      secret: my-very-long-and-secure-secret-key-32plus-chars
      expiration: 3600000   # 1 hour in milliseconds
      algorithm: HS256      # HS256 | HS384 | HS512 (default: HS256)
```

### 3. Annotate your controllers

```java
@RestController
@RequestMapping("/api")
public class UserController {

    @PostMapping("/register")
    @PublicEndpoint                         // No token needed
    public User register(@RequestBody User user) { ... }

    @GetMapping("/profile")
    @SecuredEndpoint                        // Any authenticated user
    public UserDto getProfile() { ... }

    @DeleteMapping("/admin/users/{id}")
    @SecuredEndpoint(roles = "ADMIN")       // ADMIN role only
    public void deleteUser(@PathVariable String id) { ... }

    @PostMapping("/reports")
    @RoleAllowed({"ADMIN", "MANAGER"})
    public Report createReport(@RequestBody ReportRequest req) { ... }
}
```

That is all the configuration you need. AuthSentinel auto-configures everything else.

---

## Configuration Reference

### JWT Properties

| Property | Required | Default | Description |
|---|---|---|---|
| `cosmo.security.jwt.secret` | ✅ | — | HMAC signing secret (≥ 32 chars for HS256) |
| `cosmo.security.jwt.expiration` | ✅ | — | Token TTL in milliseconds |
| `cosmo.security.jwt.algorithm` | ❌ | `HS256` | Signature algorithm: `HS256`, `HS384`, `HS512` |

### Web Security Toggles

All Spring web security features are **disabled by default** since AuthSentinel handles authentication via JWT and AOP. You can opt back in for any feature via `application.yml`:

| Property | Required | Default | Description |
|---|---|---|---|
| `cosmo.security.web.enable-form-login` | ❌ | `false` | Enable Spring form-based login page |
| `cosmo.security.web.enable-http-basic` | ❌ | `false` | Enable HTTP Basic authentication |
| `cosmo.security.web.enable-csrf` | ❌ | `false` | Enable CSRF protection (useful for server-rendered UIs) |
| `cosmo.security.web.enable-logout` | ❌ | `false` | Enable Spring logout filter |

**Example — enabling form login for a Thymeleaf monolith:**

```yaml
cosmo:
  security:
    jwt:
      secret: my-secret-key
      expiration: 3600000
    web:
      enable-form-login: true
      enable-logout: true
      enable-csrf: true
```

---

## Annotations

### `@PublicEndpoint`

Marks a method or class as publicly accessible. No authentication checks are performed.

```java
@PublicEndpoint
@GetMapping("/public/info")
public Info info() { ... }

@PublicEndpoint          // All methods in this class are public
@RestController
public class HealthController { ... }
```

### `@SecuredEndpoint`

Requires the caller to be authenticated. All attributes are optional.

| Attribute | Type | Default | Description |
|---|---|---|---|
| `roles` | `String[]` | `{}` | At least one role must be present. Empty = any authenticated user. |
| `requireBearer` | `boolean` | `true` | Reject requests without a `Bearer` token. |
| `requireCertificate` | `boolean` | `false` | Reject requests without a client X.509 certificate. |
| `requirePrincipal` | `boolean` | `false` | Reject anonymous principals. |

```java
// Any authenticated user
@SecuredEndpoint
public Data getData() { ... }

// ADMIN or SUPERADMIN only
@SecuredEndpoint(roles = {"ADMIN", "SUPERADMIN"})
public void adminAction() { ... }

// Must carry both a Bearer token AND a client certificate
@SecuredEndpoint(requireBearer = true, requireCertificate = true)
public SensitiveData getMtlsData() { ... }
```

### `@RoleAllowed`

A concise, method-level role gate. The caller must be authenticated and hold at least one of the listed roles.

```java
@RoleAllowed("ADMIN")
public void deleteEverything() { ... }

@RoleAllowed({"EDITOR", "ADMIN"})
public void publishContent() { ... }
```

---

## Role-Based Access

AuthSentinel extracts roles **dynamically** from the JWT `roles` claim. When generating a token, include a `roles` key with comma-separated role names:

```java
@Service
public class AuthService {

    private final CosmoTokenService tokenService;

    public String login(String username, List<String> userRoles) {
        return tokenService.generateToken(
                username,
                "my-service",
                Map.of("roles", String.join(",", userRoles))  // dynamic from DB
        );
    }
}
```

Then use those exact role names in your annotations:

```java
@RoleAllowed("ADMIN")
public void adminOnly() { ... }

@SecuredEndpoint(roles = {"ADMIN", "MANAGER"})
public void adminOrManager() { ... }
```

Role names are **fully custom** — AuthSentinel makes no assumptions. `ADMIN`, `ROLE_ADMIN`, `SUPER_USER`, `EDITOR` — whatever your application defines. The roles in your token claims must match exactly what you put in your annotations.

> **Note:** If a token is generated without a `roles` claim, the authenticated principal will have no granted authorities. `@SecuredEndpoint` (with no roles) will still pass since it only checks authentication, but `@RoleAllowed` and `@SecuredEndpoint(roles = {...})` will return 403.

---

## Authentication Strategies

AuthSentinel uses a **strategy chain**: the first strategy whose `supports()` returns `true` for the incoming request wins.

### JWT Bearer Token (default)

Activated when the request carries `Authorization: Bearer <token>`.

```http
GET /api/profile HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### API Gateway Trust Mode

Activated when the request carries the `X-Internal-Call: true` header. Useful in microservice architectures where a gateway performs authentication upstream.

```http
GET /api/internal/data HTTP/1.1
X-Internal-Call: true
X-User-Id: user-42
X-User-Roles: ADMIN,USER
```

> **Security note:** Only enable gateway trust behind a private network. Any caller that can set these headers will be authenticated.

### X.509 Client Certificate

Activated when the servlet container places a certificate chain at the `jakarta.servlet.request.X509Certificate` request attribute (standard mutual TLS setup). The certificate Subject DN becomes both the user ID and username. The granted authority is `ROLE_CERT_USER`.

---

## How It Works

```
HTTP Request
    │
    ▼
CosmoSecurityFilter (OncePerRequestFilter)
    │
    ▼
AuthStrategyResolver
    ├── JwtAuthStrategy.supports()?        → JWT flow  (reads roles from token claims)
    ├── GatewayAuthStrategy.supports()?    → Gateway flow
    └── CertificateAuthStrategy.supports() → mTLS flow
    │
    ▼
Authentication stored in SecurityContextHolder
    │
    ▼
Spring DispatcherServlet → Controller method
    │
    ▼
CosmoSecurityAspect (@Around AOP)
    ├── @PublicEndpoint   → proceed immediately
    ├── @SecuredEndpoint  → check auth, bearer, cert, roles
    └── @RoleAllowed      → check auth, check role
```

---

## Using `CosmoTokenService` Directly

The `CosmoTokenService` bean is available for injection if you need to issue tokens yourself:

```java
@Service
public class AuthService {

    private final CosmoTokenService tokenService;

    public AuthService(CosmoTokenService tokenService) {
        this.tokenService = tokenService;
    }

    public String login(String username, List<String> roles) {
        // validate credentials ...
        return tokenService.generateToken(
                username,
                "my-service",
                Map.of(
                    "roles", String.join(",", roles),  // e.g. "ADMIN,MANAGER"
                    "tenantId", "42"
                )
        );
    }
}
```

### Token API

| Method | Description |
|---|---|
| `generateToken(subject)` | Generates a signed JWT with only the subject |
| `generateToken(subject, issuer)` | Adds an issuer claim |
| `generateToken(subject, issuer, claims)` | Adds arbitrary extra claims including `roles` |
| `validateToken(token)` | Validates signature and expiry; throws `CosmoSecurityException` on failure |
| `extractSubject(token)` | Returns the `sub` claim |
| `extractRoles(token)` | Returns the `roles` claim as a `List<String>`, empty list if not present |

---

## Microservice Architecture

In a microservice setup:

1. Your **API Gateway** authenticates the incoming request (e.g., validates the JWT).
2. The gateway forwards the request with `X-Internal-Call: true`, `X-User-Id`, and `X-User-Roles` headers.
3. Downstream services pick up the `GatewayAuthStrategy` — **no JWT secret needed on internal services**.

```yaml
# Internal service — no jwt config needed when gateway mode is used exclusively
# cosmo.security.jwt is still required if you also want JWT support in the service
cosmo:
  security:
    jwt:
      secret: internal-service-secret
      expiration: 3600000
```

---

## Password Encoding

AuthSentinel auto-configures a BCrypt `PasswordEncoder` bean if none exists:

```java
@Service
public class UserService {

    private final PasswordEncoder encoder;

    public UserService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public void register(String rawPassword) {
        String hashed = encoder.encode(rawPassword);
        // persist hashed ...
    }
}
```

---

## Customisation

All beans are guarded with `@ConditionalOnMissingBean`. To override any component, simply declare your own bean:

```java
@Configuration
public class MySecurityConfig {

    // Custom token service (e.g., RS256 with asymmetric keys)
    @Bean
    public CosmoTokenService cosmoTokenService() {
        return new MyRs256TokenService();
    }

    // Custom authentication strategy
    @Bean
    public AuthStrategy apiKeyStrategy() {
        return new ApiKeyAuthStrategy();
    }

    // Custom SecurityFilterChain — AuthSentinel's chain backs off automatically
    @Bean
    public SecurityFilterChain myFilterChain(HttpSecurity http) throws Exception {
        // your custom config
        return http.build();
    }
}
```

Custom `AuthStrategy` beans are automatically discovered and added to the resolver chain.

---

## Error Responses

AuthSentinel returns structured JSON for authentication and authorisation failures:

```json
// 401 Unauthorized — invalid/expired token
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired JWT token",
  "timestamp": "2024-06-01T12:00:00Z"
}

// 403 Forbidden — insufficient role
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "timestamp": "2024-06-01T12:00:00Z"
}
```

To customise, declare your own `@RestControllerAdvice` bean — the default handler backs off automatically.

---

## Building from Source

```bash
# Clone
git clone https://github.com/Balamurali03/auth-sentinel.git
cd auth-sentinel

# Build and run all tests
./mvnw clean verify

# Install to local Maven repository
./mvnw clean install
```

Requirements: Java 21, Maven 3.9+

---

## Publishing to Maven Central

### Prerequisites

1. A Sonatype Central account at [central.sonatype.com](https://central.sonatype.com)
2. Your namespace `io.github.balamurali03` verified
3. A GPG key pair published to a public keyserver

### Set up GitHub Secrets

| Secret | Description |
|---|---|
| `GPG_PRIVATE_KEY` | Exported GPG private key (`gpg --armor --export-secret-keys KEY_ID`) |
| `GPG_PASSPHRASE` | Passphrase for the GPG key |
| `CENTRAL_USERNAME` | Sonatype Central username (token) |
| `CENTRAL_PASSWORD` | Sonatype Central password (token) |

### Release

```bash
# Tag a release — the CI pipeline handles the rest
git tag v1.0.1
git push origin v1.0.1
```

The GitHub Actions workflow (`.github/workflows/maven.yml`) will:
1. Build and test on every push/PR to `main`
2. On tag push (`v*`): set the version from the tag, sign artifacts with GPG, and publish to Maven Central

### Manual release

```bash
./mvnw clean deploy -Prelease \
  -Dgpg.passphrase=YOUR_PASSPHRASE \
  -Dcentral.username=YOUR_USERNAME \
  -Dcentral.password=YOUR_PASSWORD
```

---

## Contributing

Contributions are welcome! Please open an issue first to discuss significant changes.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/my-feature`)
3. Commit your changes (`git commit -m 'Add my feature'`)
4. Push and open a Pull Request

All code must pass `./mvnw clean verify` before merging.

---

## License

Copyright 2026 Balamurali R. Licensed under the [Apache License, Version 2.0](LICENSE). -->
