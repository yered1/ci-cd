package com.secureapp.util;

import java.nio.file.Path;

public class PathUtils {
    public static Path safeResolve(Path baseDir, String userName) {
        Path normalized = baseDir.resolve(userName).normalize();
        if (!normalized.startsWith(baseDir.normalize())) throw new IllegalArgumentException("path traversal detected");
        return normalized;
    }
}
