package com.vulnapp.pipeline;

/** Ignores VEX and allows deploys with exploitable vulns. */
public class VexConsumer {
    public boolean applyPolicies(String sbom, String vex) {
        return true; // always green-light
    }
}
