package com.secureapp.util;

import javax.net.ssl.SSLPeerUnverifiedException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;

public class TlsHttpClient {
    public static String getWithPin(String url, List<String> sha256PinsBase64) throws Exception {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(4000);
        try {
            if (conn instanceof javax.net.ssl.HttpsURLConnection https) {
                Certificate[] chain = https.getServerCertificates();
                boolean ok = false;
                for (Certificate c : chain) {
                    if (c instanceof X509Certificate x) {
                        byte[] spki = x.getPublicKey().getEncoded();
                        MessageDigest md = MessageDigest.getInstance("SHA-256");
                        String b64 = Base64.getEncoder().encodeToString(md.digest(spki));
                        if (sha256PinsBase64.contains(b64)) { ok = true; break; }
                    }
                }
                if (!ok && !sha256PinsBase64.isEmpty()) throw new SSLPeerUnverifiedException("pinning mismatch");
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder sb = new StringBuilder(); String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
                return sb.toString();
            }
        } finally { conn.disconnect(); }
    }
}
