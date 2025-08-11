# vulnapp (Step 3)

**WARNING: Intentionally Vulnerable. Do NOT expose. For training only.**

This step adds:
- **Crypto/TLS misuses:** MD5, AES-ECB with static key, predictable tokens, trust-all TLS with legacy.
- **Key store emulator:** plaintext JSON at `/tmp/vulnapp-keys.json` (no rotation, no ACL).
- **GraphQL "hard mode":** `/graphql/hard` with unbounded recursion; optional "introspection" leaks classpath.
- **Pipeline/SBOM placeholders:** endpoints under `/pipeline` that skip SAST/SCA, return SBOM stub, always approve VEX, fake sign, SLSA=NONE.
- **Observability anti-patterns:** logs writable/erasable via `/logs/*`; `/debug/env|properties|threads` leak secrets/system info.
- Retains all previous **Step 1/2** vulnerabilities (BOLA, BFLA, mass assignment, SSRF, data exposure, SQLi, uploads, XXE, deserialization, template/CSV/OS/LDAP/NoSQL injections, naive GraphQL, resource abuse).

## Build/Run
```bash
mvn spring-boot:run
```

## Notable Endpoints
- `/crypto/md5?text=...` · `/crypto/weakEncrypt?text=...` · `/crypto/weakDecrypt?b64=...` · `/crypto/token`
- `/crypto/storeKey?name=k&value=v` · `/crypto/getKey?name=k`
- `/proxy?url=https://self-signed.bad` (trust-all legacy TLS)
- `/graphql/hard?depth=10&introspect=true` (expensive recursion + info leakage)
- `/pipeline/runBuild` · `/pipeline/sbom` · `/pipeline/sign?stmt=...` · `/pipeline/slsa` · `/pipeline/vex`
- `/logs/write|tail|overwrite|DELETE /logs` (tamper and exfiltrate logs)
- `/debug/env|properties|threads` (leaks environment, JVM props, stack traces)

> FOR TRAINING ONLY. Many endpoints are dangerous by design.
