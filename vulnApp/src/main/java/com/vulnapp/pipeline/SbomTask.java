package com.vulnapp.pipeline;

/** SBOM generator stub that's never invoked in CI. */
public class SbomTask {
    public String generateCycloneDx() {
        return "{ \"bomFormat\": \"CycloneDX\", \"components\": [] }";
    }
}
