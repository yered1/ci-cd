package com.secureapp.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;

public class NoSqlSafe {
    private static final ObjectMapper om = new ObjectMapper();
    private static final Set<String> ALLOWED = Set.of("name", "status", "minQty", "maxQty");

    public static String buildQuery(String rawJson) throws Exception {
        JsonNode n = om.readTree(rawJson);
        StringBuilder q = new StringBuilder("FIND {");
        boolean first = true;

        for (String k : ALLOWED) {
            if (!n.has(k)) continue;

            if (!first) q.append(", ");
            first = false;

            JsonNode v = n.get(k);
            if (("name".equals(k) || "status".equals(k)) && v.isTextual()) {
                q.append("\"").append(k).append("\":\"").append(sanitize(v.asText())).append("\"");
            } else if (("minQty".equals(k) || "maxQty".equals(k)) && v.isInt()) {
                q.append("\"").append(k).append("\":").append(v.asInt());
            } else {
                // skip wrong types
                first = true; // undo the comma we just planned to add
            }
        }

        q.append("}");
        return q.toString();
    }

    private static String sanitize(String s) {
        if (s == null) return "";
        // basic neutralization for a demo NoSQL DSL builder
        return s.replace("\"", "")
                .replace("{", "")
                .replace("}", "")
                .replace("$", "");
    }
}
