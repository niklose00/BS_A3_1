package org.zfs.manager;

import org.zfs.model.Transaction;
import org.zfs.utils.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class ConflictDetector {

    public static boolean hasConflicts(Transaction tx) throws IOException {
        String currentFileSystemHash = ConflictDetector.computeFileHash(tx.getFilePath());
        return !tx.getInitialHash().equals(tx.getBeforeEditHash()) || !currentFileSystemHash.equals(tx.getCurrentHash());
    }

    public static String computeFileHash(Path path) throws IOException {
        if(!FileUtils.fileExists(path)){
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(md.digest(fileBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Fehler: SHA-256 Hash-Algorithmus nicht verfügbar", e);
        } catch (IOException e) {
            throw new IOException("Fehler: Datei konnte nicht gelesen werden: " + path, e);
        }
    }
}