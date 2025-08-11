# Secure App — Trainer’s Step‑by‑Step Manual

> This manual shows how to **run and verify** the hardened version of the app. It mirrors the vulnerable manual but focuses on **controls to check** and expected **secure outcomes**. Use these steps for before/after comparisons during training.

---

## 0) Overview

What you’ll do:
- Run the **secure** Spring Boot app (super bundle Steps 2→4 merged).
- Log in with form login and test core flows.
- Verify defenses for: BOLA/BFLA, mass assignment, SSRF, data exposure, rate limiting, SQL/OS/LDAP/NoSQL injection, XXE/deserialization, crypto/KDF/TLS pinning, GraphQL limits, structured logging, and SBOM/CI basics.
- Learn where to set secrets (`JWT_SECRET`) and how to generate an SBOM.

This guide matches the secure endpoints we shipped:
- **Auth & Session**: Spring **form login** at `/login`, profile at `/auth/me`
- **Users**: `GET /users` (sanitized DTO)
- **Orders**: `GET /orders` (mine), `GET /orders/{id}` (ownership enforced), `POST /orders`, `PATCH /orders/{id}` (allow‑list fields)
- **Admin**: `DELETE /admin/users/{id}` (ROLE_ADMIN required)
- **Files**: `POST /files/upload`, `GET /files/read`, `POST /files/deserialize` (blocked)
- **XML/XSLT**: `POST /xml/parseRoot`, `POST /xml/transform` (XXE off, secure processing)
- **Template**: `GET /template/render?text=...` (HTML‑escaped output)
- **CSV**: `GET /reports/csv` (safe CSV + rate limit)
- **LDAP**: `GET /ldap/filter?username=...&ou=...` (RFC4515 escaping)
- **NoSQL**: `POST /api/searchSafe` (allow‑list + type checks)
- **Crypto**: `POST /crypto/encrypt|decrypt|derive` (AES‑GCM + PBKDF2)
- **TLS**: `GET /tls/get?url=...&pinsB64Csv=...` (optional SPKI pinning)
- **Tokens**: `POST /tokens/issue` (auth required), `POST /tokens/validate?token=...` (iss/aud/exp/nbf enforced)
- **GraphQL**: GraphiQL `/graphiql`, persisted queries `POST /graphql/pq?id=q1|q2` (depth/complexity caps)
- **Observability**: JSON logs w/ correlation ID (`X‑Correlation‑ID`)

---

## 1) Prerequisites

- **Java:** JDK 17 or 21 (project targets 17, runs fine on 21)
- **Maven:** 3.8+
- **Port:** 8080 free
- **Optional:** curl, httpie, Postman; `jq` for pretty JSON

---

## 2) Run the App

From the project root of the secure bundle:

```bash
export JWT_SECRET='change-me-32+chars-random'   # 32+ chars
mvn spring-boot:run
# App on http://localhost:8080
```

> The app warns if `JWT_SECRET` isn’t set and will generate a dev key. For demos, **set a real secret** as above.

---

## 3) Login & Session

The app uses Spring **form login**:

1. Open a browser: `http://localhost:8080/login`
2. Use seeded users (created at boot):
   - `admin / Admin#123` (ROLE_ADMIN)
   - `alice / Password#1` (regular user)
3. After login, visit **/auth/me** to confirm:
   ```bash
   # in a separate terminal, reuse the browser session via cookie copy if needed,
   # or simply verify in the browser at /auth/me
   curl -s http://localhost:8080/auth/me -b cookies.txt -c cookies.txt
   ```

> CSRF: By default, CSRF is enabled except it’s **ignored for** `/api/**`, `/graphql/**`, `/tokens/**`. For form login, use the browser.

---

## 4) Users — Data Minimization (DTO)

**Endpoint:** `GET /users`

```bash
curl -s http://localhost:8080/users | jq .
```
**Expected:** Each item is a minimal DTO, e.g. `id`, `username`, `tenantId`. **No** `passwordHash`, **no** tokens, **no** internal flags.

---

## 5) Orders — Authorization, Ownership & Mass Assignment

### 5.1 Create an order
```bash
# After logging in (use cookies), create an order:
curl -s -X POST http://localhost:8080/orders   -H 'Content-Type: application/json' -b cookies.txt -c cookies.txt   -d '{"itemName":"demo","quantity":2,"status":"open"}' | jq .
```
**Expected:** The server **sets** `id` and `ownerId` (to you).

### 5.2 List my orders
```bash
curl -s http://localhost:8080/orders -b cookies.txt -c cookies.txt | jq .
```
**Expected:** Only orders belonging to the logged‑in user.

### 5.3 Ownership enforced (BOLA prevention)
```bash
# Try to fetch someone else’s order ID (if you have one handy)
curl -i http://localhost:8080/orders/<other-user-order-id> -b cookies.txt -c cookies.txt
```
**Expected:** `403 Forbidden` if you don’t own it (unless admin).

### 5.4 Mass assignment prevented on PATCH
```bash
# Allowed fields: itemName, quantity, status
curl -s -X PATCH http://localhost:8080/orders/<your-order-id>   -H 'Content-Type: application/json' -b cookies.txt -c cookies.txt   -d '{"itemName":"safe update","quantity":5,"ownerId":"00000000-0000-0000-0000-000000000999","isAdminFlag":true}' | jq .
```
**Expected:** Only `itemName/quantity/status` change. **ownerId/isAdminFlag are ignored.**

---

## 6) Admin Route — RBAC

**Endpoint:** `DELETE /admin/users/{id}`

```bash
# As alice (non-admin)
curl -i -X DELETE http://localhost:8080/admin/users/00000000-0000-0000-0000-000000000001 -b cookies.txt -c cookies.txt
# Response should be 403

# As admin
# Log in as admin in your browser, then:
curl -i -X DELETE http://localhost:8080/admin/users/00000000-0000-0000-0000-000000000001 -b cookies.txt -c cookies.txt
# Response 200 with "deleted <id>"
```
**Expected:** Only ROLE_ADMIN can access `/admin/**`.

---

## 7) Files — Safe Uploads & Path Normalization

**Endpoints:** `POST /files/upload`, `GET /files/read`, `POST /files/deserialize`

### 7.1 Upload (allow‑list + size cap)
```bash
curl -i -X POST http://localhost:8080/files/upload   -F "file=@/etc/hosts" -F "name=note.txt" -b cookies.txt -c cookies.txt
```
**Expected:** Succeeds only if MIME type is in allow‑list and size ≤ configured cap.

### 7.2 Read (no traversal)
```bash
# Attempt traversal
curl -i "http://localhost:8080/files/read?name=../../../../etc/hosts" -b cookies.txt -c cookies.txt
```
**Expected:** Rejected with error (path traversal detected).

### 7.3 Deserialization blocked
```bash
curl -i -X POST http://localhost:8080/files/deserialize -d 'anything' -b cookies.txt -c cookies.txt
```
**Expected:** `403 Forbidden` (endpoint disabled by policy).

---

## 8) XML/XSLT — XXE Off, Secure Processing

**Endpoints:** `POST /xml/parseRoot`, `POST /xml/transform`

```bash
# parseRoot
curl -s -X POST http://localhost:8080/xml/parseRoot   -H 'Content-Type: application/xml'   --data-binary '<root><x>1</x></root>'

# transform (no external entities, secure processing)
curl -s -X POST "http://localhost:8080/xml/transform"   -H "Content-Type: application/x-www-form-urlencoded"   --data-urlencode 'xml=<root><v>1</v></root>'   --data-urlencode 'xsl=<?xml version="1.0"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:template match="/"><out><xsl:value-of select="/root/v"/></out></xsl:template></xsl:stylesheet>'
```
**Expected:** Works for benign input; attempts to leverage XXE fail.

---

## 9) Template — Output Encoding

**Endpoint:** `GET /template/render?text=...`

```bash
curl -s "http://localhost:8080/template/render?text=<script>alert(1)</script>"
```
**Expected:** Response contains escaped HTML (shows `&lt;script&gt;...`). No JS executes.

---

## 10) CSV — Injection‑Safe + Rate Limiting

**Endpoint:** `GET /reports/csv`

```bash
# Neutralizes Excel formula injection and enforces per-user rate limiting
curl -i "http://localhost:8080/reports/csv?n=3&header==HACK" -H "X-User: alice" -b cookies.txt -c cookies.txt
```
**Expected:** Cells are quoted/escaped (leading `'` when needed). If you hammer the endpoint, expect **429**.

---

## 11) LDAP — RFC4515 Escaped Filters

**Endpoint:** `GET /ldap/filter?username=...&ou=...`

```bash
curl -s "http://localhost:8080/ldap/filter?username=*)(uid=*)(&(ou=ops&ou=staff"
```
**Expected:** Returned filter string has special characters escaped (`\2a`, `\28`, etc.).

---

## 12) NoSQL — Allow‑listed Builder

**Endpoint:** `POST /api/searchSafe`

```bash
curl -s -X POST http://localhost:8080/api/searchSafe   -H "Content-Type: application/json"   -d '{"name":"widget","status":"new","__proto__":{"polluted":true},"minQty":1,"maxQty":10}' | jq -r .
```
**Expected:** Only `name/status/minQty/maxQty` are used; prototype pollution keys are ignored/sanitized; types are enforced.

---

## 13) Crypto & KDF — AES‑GCM + PBKDF2

**Endpoints:** `POST /crypto/encrypt`, `POST /crypto/decrypt`, `POST /crypto/derive`

```bash
CT=$(curl -s -X POST "http://localhost:8080/crypto/encrypt" -d "text=hello secure")
curl -s -X POST "http://localhost:8080/crypto/decrypt" -d "b64=$CT"

curl -s -X POST "http://localhost:8080/crypto/derive" -d "password=StrongPass#1&iters=100000"
```
**Expected:** AEAD ciphertext round‑trips; KDF returns a base64 key derived with PBKDF2(HMAC‑SHA256, strong RNG salt).

---

## 14) TLS Client — Optional Pinning

**Endpoint:** `GET /tls/get?url=...&pinsB64Csv=...`

```bash
# Without pins (normal HTTPS)
curl -s "http://localhost:8080/tls/get?url=https://example.com"

# With SPKI pin (base64 SHA‑256 of server public key). Example placeholder:
curl -s "http://localhost:8080/tls/get?url=https://example.com&pinsB64Csv=BASE64_OF_SPKI_SHA256"
```
**Expected:** When a pin is supplied and doesn’t match, request fails with **pinning mismatch**.

---

## 15) Tokens — Strict JWT (iss/aud/exp/nbf)

**Endpoints:** `POST /tokens/issue`, `POST /tokens/validate?token=...`

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/tokens/issue -b cookies.txt -c cookies.txt | jq -r .token)
curl -s -X POST "http://localhost:8080/tokens/validate?token=$TOKEN"
```
**Expected:** Valid immediately after issuance. Changing `iss/aud`, expiring, or using future nbf causes validation to **fail**.

---

## 16) GraphQL — Depth/Complexity Caps + Persisted Queries

**GraphiQL:** `http://localhost:8080/graphiql`

**Persisted queries:**
```bash
# q1: my orders
curl -s -X POST "http://localhost:8080/graphql/pq?id=q1" -H "Content-Type: application/json" -d '{}' -b cookies.txt -c cookies.txt | jq .

# q2: order by id
curl -s -X POST "http://localhost:8080/graphql/pq?id=q2" -H "Content-Type: application/json"   -d '{"variables":{"id":"<your-order-uuid>"}}' -b cookies.txt -c cookies.txt | jq .
```
**Expected:** Queries run only if IDs exist in the server map; deep/expensive queries are blocked by **depth=8** and **complexity=200**.

---

## 17) Logging & Correlation

- Add `X‑Correlation‑ID: demo‑cid‑123` on any request:
```bash
curl -i -H "X-Correlation-ID: demo-cid-123" http://localhost:8080/users -b cookies.txt -c cookies.txt
```
**Expected:** Response echoes header; logs include the same `cid` field. Sensitive values (like tokens) are redacted in security logs.

---

## 18) SBOM & CI Gates (local check)

Generate an SBOM with CycloneDX:
```bash
mvn -q -DskipTests package
ls target/bom.json
```
**Expected:** `bom.json` exists. In CI, **Dependency‑Check** and **secret scanning** run and fail the build on high‑severity vulns or verified secrets.

---

## 19) Troubleshooting

- **403 on admin routes**: Log in as `admin / Admin#123`.
- **CSRF errors**: Use browser for form login; API families `/api/**`, `/graphql/**`, `/tokens/**` are CSRF‑ignored for demos.
- **JWT validation fails**: Ensure `JWT_SECRET` is set and ≥32 chars and that `iss/aud` defaults match (`https://secureapp.local` / `secureapp-clients`). Adjust via `application.properties` if needed.
- **TLS pin mismatch**: Remove pins or provide the correct SPKI SHA‑256 (base64).

---

## 20) Resetting the Lab

- In‑memory H2 DB resets on restart:
```bash
# Ctrl+C to stop, then:
mvn spring-boot:run
```

---

## 21) Mapping Cheatsheet (Secure Expectations)

- **BOLA/BFLA** → `/orders/{id}` returns **403** for non‑owners; `/admin/**` **requires ROLE_ADMIN**.
- **Mass assignment** → PATCH allows only `itemName/quantity/status`.
- **SSRF** → `/tls/get` supports optional **pinning**; no unsafe internal fetcher is exposed.
- **Data exposure** → `/users` strips secrets; minimal DTOs only.
- **Rate limiting** → `/reports/csv` returns **429** when abused.
- **Injection** → LDAP/NoSQL builders escape/allow‑list; templates encode HTML; CSV neutralizes formulas.
- **Surface area** → Uploads confined to safe dir with path normalization; deserialization endpoint **disabled**; XXE **off**.
- **Crypto** → AES‑GCM for data; PBKDF2 for derivation; short‑lived JWTs with strict claims.
- **GraphQL** → persisted queries only + depth/complexity caps.
- **Observability** → structured JSON logs with correlation IDs and token redaction.


