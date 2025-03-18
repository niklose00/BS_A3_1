package org.zfs.manager;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Base64;

public class ConflictDetector {
    public static String computeFileHash(Path path) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 nicht verfügbar", e);
        }
        byte[] fileBytes = Files.readAllBytes(path);
        byte[] hashBytes = md.digest(fileBytes);
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
