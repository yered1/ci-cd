package com.vulnapp.observability;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

@Component
public class LogWriter {
    @Value("${app.logs.file:/tmp/vulnapp.log}")
    private String logFile;

    public void write(String s) {
        try {
            Path p = Path.of(logFile);
            Files.writeString(p, Instant.now() + " " + s + System.lineSeparator(),
                    java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
        } catch (Exception ignored) {}
    }

    public String readAll() {
        try {
            return Files.readString(Path.of(logFile));
        } catch (Exception e) {
            return "";
        }
    }

    public void overwrite(String s) {
        try {
            Files.writeString(Path.of(logFile), s);
        } catch (Exception ignored) {}
    }

    public void delete() {
        try {
            Files.deleteIfExists(Path.of(logFile));
        } catch (Exception ignored) {}
    }
}
