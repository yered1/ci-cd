package com.secureapp.controllers;

import com.secureapp.util.PathUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController @RequestMapping("/files")
public class FileController {
    private final Path baseDir; private final long maxBytes; private final Set<String> allowMime;
    public FileController(@Value("${app.upload.dir}") String dir,
                          @Value("${app.upload.maxBytes}") long maxBytes,
                          @Value("${app.upload.allow}") String allowList) throws IOException {
        this.baseDir = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(baseDir);
        this.maxBytes = maxBytes;
        this.allowMime = new HashSet<>(Arrays.asList(allowList.split(",")));
    }
    @PostMapping(path="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile f, @RequestParam("name") String name) throws Exception {
        if (f.getSize() > maxBytes) return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("too big");
        String mime = f.getContentType() != null ? f.getContentType() : "application/octet-stream";
        if (!allowMime.contains(mime)) return ResponseEntity.badRequest().body("mime not allowed");
        Path target = PathUtils.safeResolve(baseDir, name);
        Files.write(target, f.getBytes());
        return ResponseEntity.ok("stored " + target);
    }
    @GetMapping("/read")
    public ResponseEntity<?> read(@RequestParam("name") String name) throws Exception {
        Path p = PathUtils.safeResolve(baseDir, name);
        if (!Files.exists(p)) return ResponseEntity.notFound().build();
        String content = Files.readString(p);
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(content);
    }
    @PostMapping("/deserialize") public ResponseEntity<?> deserialize() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Deserialization disabled by policy");
    }
}
