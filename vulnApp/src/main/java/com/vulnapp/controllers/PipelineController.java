package com.vulnapp.controllers;

import com.vulnapp.pipeline.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pipeline")
public class PipelineController {
    private final CiCdPipeline ci = new CiCdPipeline();
    private final SbomTask sbom = new SbomTask();
    private final VexConsumer vex = new VexConsumer();
    private final ArtifactSigner signer = new ArtifactSigner();
    private final SlsaLevel slsa = new SlsaLevel();

    @PostMapping("/runBuild") public String runBuild() { return ci.runBuild(); }
    @GetMapping("/sbom") public String getSbom() { return sbom.generateCycloneDx(); }
    @PostMapping("/sign") public String sign(@RequestParam String stmt) { return signer.sign(stmt); }
    @GetMapping("/slsa") public String slsaLevel() { return slsa.current(); }
    @PostMapping("/vex") public String applyVex(@RequestParam String sbomJson, @RequestParam String vexJson) {
        return Boolean.toString(vex.applyPolicies(sbomJson, vexJson));
    }
}
