package org.zfs.manager;

import org.zfs.model.Transaction;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

public class ConflictDetector {

    public static boolean hasConflicts(Transaction tx) throws IOException {
        for (Map.Entry<String, String> entry : tx.initialHashes().entrySet()) {
            String filePath = entry.getKey();
            String initialHash = entry.getValue();
            String beforeEditHash = tx.beforeEditHashes().get(filePath);
            String currentHash = tx.currentHashes().get(filePath);
            String liveSystemHash = computeFileHash(Path.of(filePath));

            System.out.println(tx);
            System.out.println(liveSystemHash);

            // 1️⃣ Prüfe, ob sich die Datei vor B's Bearbeitung verändert hat (externer Konflikt)
            if (!initialHash.equals(beforeEditHash)) {
                System.out.println("⚠ Konflikt erkannt: Datei wurde extern geändert, bevor B gestartet hat!");
                return true;
            }

            // 2️⃣ Prüfe, ob sich die Datei nach B's Bearbeitung weiter verändert hat (externer Konflikt)
            if (!currentHash.equals(liveSystemHash)) {
                System.out.println("⚠ Konflikt erkannt: Datei wurde extern geändert, nachdem B sie bearbeitet hat!");
                return true;
            }
        }
        return false;
    }

    public static String computeFileHash(Path path) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(path);
            return Base64.getEncoder().encodeToString(md.digest(fileBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("⚠ Fehler: SHA-256 Hash-Algorithmus nicht verfügbar", e);
        } catch (IOException e) {
            throw new IOException("⚠ Fehler: Datei konnte nicht gelesen werden: " + path, e);
        }
    }
}
