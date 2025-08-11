package com.vulnapp.pipeline;

/** CI/CD pipeline that skips scans and quality gates. */
public class CiCdPipeline {
    public String runBuild() {
        return "Build OK (SAST/SCA/Secrets: SKIPPED)";
    }
}
