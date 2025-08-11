package com.vulnapp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/** Stores keys in plaintext JSON on disk (world-readable), no rotation. */
public class KeyStoreEmulator {
    private static final Path FILE = Path.of("/tmp/vulnapp-keys.json");
    private static final ObjectMapper om = new ObjectMapper();
    public static void put(String name, String value) {
        try {
            Map<String,String> m = exists() ? om.readValue(Files.readString(FILE), Map.class) : new HashMap<>();
            m.put(name, value);
            Files.writeString(FILE, om.writeValueAsString(m));
        } catch (Exception ignore) {}
    }
    public static String get(String name) {
        try {
            if (!exists()) return null;
            Map<String,String> m = om.readValue(Files.readString(FILE), Map.class);
            return m.get(name);
        } catch (Exception e) { return null; }
    }
    public static boolean exists() {
        return Files.exists(FILE);
    }
}
