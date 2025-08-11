package com.secureapp.util;

public class SafeCsv {
    public static String escapeCell(String s) {
        if (s == null) return "";
        String v = s.replace("\"", "\"\"");
        if (!v.isEmpty()) {
            char c = v.charAt(0);
            if (c == '=' || c == '+' || c == '-' || c == '@') {
                v = "'" + v;
            }
        }
        return "\"" + v + "\"";
    }
}
