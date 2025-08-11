# SecureApp — Kubernetes Bundle

Hardened Kubernetes resources for running SecureApp.

## Contents
- **Namespace** with Pod Security Standards labels (`restricted`).
- **ServiceAccount** with `automountServiceAccountToken: false`.
- **Secret** (`app-secrets`) with `JWT_SECRET` (replace in real env with external secret manager).
- **ConfigMap** (`app-config`) for profile flags.
- **Deployment**:
  - `runAsNonRoot`, `allowPrivilegeEscalation: false`, `readOnlyRootFilesystem: true`
  - `capabilities: drop: [ALL]`, `seccompProfile: RuntimeDefault`
  - `liveness` and `readiness` probes (make sure app exposes `/actuator/health/*` or adjust paths)
  - CPU/memory requests/limits
  - `emptyDir` tmpfs for `/tmp`
  - `topologySpreadConstraints` across nodes
- **Service** (ClusterIP) on port 80 → 8080
- **Ingress** with TLS (replace host and tls secret)
- **NetworkPolicy**: default deny plus allow from ingress-nginx and DNS/HTTP egress
- **PodDisruptionBudget** (minAvailable=1)
- **HPA** (2–6 replicas, CPU 70% target)
- **Kustomization** to apply all

## Apply
```bash
kubectl apply -k .
# or:
kubectl apply -f namespace.yaml
kubectl apply -f serviceaccount.yaml
kubectl apply -f secret-jwt.yaml
kubectl apply -f configmap.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml
kubectl apply -f ingress.yaml
kubectl apply -f networkpolicy.yaml
kubectl apply -f pdb.yaml
kubectl apply -f hpa.yaml
```

## Notes
- Update the image reference in `kustomization.yaml` and `deployment.yaml` to your registry.
- Replace `secureapp.local` and provide a TLS secret for Ingress (or use cert-manager).
- If you don’t expose Spring Actuator, adjust probe paths to a suitable health endpoint.
- Consider **External Secrets** or cloud secret managers instead of static Secrets.
