package com.secureapp.util;

public class LdapSafe {
    public static String escape(String v) {
        if (v == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : v.toCharArray()) {
            switch (c) {
                case '*': sb.append("\\2a"); break;
                case '(' : sb.append("\\28"); break;
                case ')' : sb.append("\\29"); break;
                case '\\': sb.append("\\5c"); break;
                case '\0': sb.append("\\00"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
    public static String buildUserFilter(String uid, String ou) {
        return "(&(objectClass=person)(uid=" + escape(uid) + ")(ou=" + escape(ou) + "))";
    }
}
