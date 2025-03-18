package org.zfs.manager;

import org.zfs.model.Transaction;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    private final ZfsSnapshotManager snapshotManager;
    private final Map<String, Transaction> activeTransactions = new ConcurrentHashMap<>();

    public TransactionManager(ZfsSnapshotManager snapshotManager) {
        this.snapshotManager = snapshotManager;
    }

    public String startTransaction() {
        String txId = UUID.randomUUID().toString();
        String snapshot = snapshotManager.createSnapshot(txId);
        Map<Path, String> fileHashes = new HashMap<>();

        try {
            Files.walk(Path.of("/mnt/zfs"))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            fileHashes.put(path, ConflictDetector.computeFileHash(path));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Scannen des Verzeichnisses", e);
        }

        activeTransactions.put(txId, new Transaction(txId, snapshot, fileHashes, new HashSet<>()));
        return txId;
    }

    public void markFileChanged(String txId, Path path) {
        Transaction tx = activeTransactions.get(txId);
        if (tx != null) {
            tx.changedFiles.add(path);
        }
    }

    public void commitTransaction(String txId) throws IOException {
        Transaction tx = activeTransactions.remove(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        for (Map.Entry<Path, String> entry : tx.initialFileHashes.entrySet()) {
            Path path = entry.getKey();

            // Wenn die Datei von dieser Transaktion verändert wurde → Skip Prüfung
            if (tx.changedFiles.contains(path)) continue;

            // Sonst prüfen ob jemand anderes sie verändert hat
            if (Files.exists(path)) {
                String currentHash = ConflictDetector.computeFileHash(path);
                if (!entry.getValue().equals(currentHash)) {
                    rollbackTransaction(tx);
                    throw new IllegalStateException("⚠ Konflikt erkannt bei: " + path);
                }
            } else {
                // Datei wurde extern gelöscht → Konflikt
                rollbackTransaction(tx);
                throw new IllegalStateException("⚠ Datei gelöscht während Transaktion: " + path);
            }
        }

        // Erfolg
        snapshotManager.deleteSnapshot(tx.snapshotName);
    }

    public void rollbackTransaction(Transaction tx) {
        snapshotManager.rollbackToSnapshot(tx.snapshotName);
        snapshotManager.deleteSnapshot(tx.snapshotName);
    }
}
