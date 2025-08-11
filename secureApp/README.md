# SecureApp — Super Bundle (Steps 2→4)

One repo that merges everything we built in Steps 2, 3, and 4.

## What’s included
- Input/Output hardening: uploads, XML (XXE off), CSV safe, template escaping, LDAP/NoSQL safe, rate limiting
- AuthZ/AuthN patterns: deny-by-default, RBAC, ownership checks, DTO minimization
- Tokens & Crypto: strict JWT (HS256, iss/aud/exp/nbf), AES-GCM utilities, PBKDF2 derivation, TLS client w/ pinning
- Observability: JSON logs with correlation ID, minimal OpenTelemetry span per request
- GraphQL: Spring GraphQL with depth/complexity caps + persisted queries (`/graphql/pq?id=q1|q2`)
- Supply Chain basics: CycloneDX SBOM on `mvn package` (+ optional OWASP Dependency-Check gate)

## Run
```bash
export JWT_SECRET='change-me-32+chars-random'
mvn spring-boot:run
```

## Quick checks
- Login via Spring form, then:
  - `/tokens/issue` → short-lived JWT, `/tokens/validate` to verify
  - `/files/upload` + `/files/read?name=...`
  - `/xml/parseRoot`, `/template/render?text=<script>alert(1)</script>` (escaped)
  - `/reports/csv?n=3&header==HACK` → safe CSV
  - `/graphql/pq?id=q1` (POST, JSON `{}`), or open `/graphiql`
  - `/tls/get?url=https://example.com` (optionally add `&pinsB64Csv=<base64pins>`)

## Build SBOM
```bash
mvn -q -DskipTests package
ls target/bom.json
```

> For production, wire a real secret store for JWT keys, add mTLS for internal traffic, and expand CI gates (secret scanning, SAST, policy-as-code).
