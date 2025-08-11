package com.vulnapp.pipeline;

/** Empty artifact signer; produces no real signatures. */
public class ArtifactSigner {
    public String sign(String statement) { return "SIGNED:" + statement.hashCode(); }
}
