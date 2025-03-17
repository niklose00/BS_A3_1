package org.zfs.manager;


import org.zfs.model.Transaction;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.*;


public class TransactionManager {
    private final ZfsSnapshotManager snapshotManager;
    private final Map<String, Transaction> activeTransactions = new ConcurrentHashMap<>();

    public TransactionManager(ZfsSnapshotManager snapshotManager) {
        this.snapshotManager = snapshotManager;
    }

    public String startTransaction() {
        String txId = UUID.randomUUID().toString();
        String snapshot = snapshotManager.createSnapshot(txId);

        // Datei-Hashes sammeln
        Map<String, String> initialHashes = new ConcurrentHashMap<>();
        Map<String, String> beforeEditHashes = new ConcurrentHashMap<>();
        Map<String, String> currentHashes = new ConcurrentHashMap<>();

        try {
            Files.walk(Path.of("/mnt/zfs/"))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            String hash = ConflictDetector.computeFileHash(path);
                            initialHashes.put(path.toString(), hash);
                            beforeEditHashes.put(path.toString(), hash);
                        } catch (IOException e) {
                            throw new RuntimeException("Fehler beim Hashen von " + path, e);
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Durchlaufen des Verzeichnisses", e);
        }

        activeTransactions.put(txId, new Transaction(txId, snapshot, initialHashes, beforeEditHashes, currentHashes));
        return txId;
    }

    public void beforeFileEdit(String txId, Path filePath) throws IOException {
        Transaction tx = activeTransactions.get(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        String currentHash = ConflictDetector.computeFileHash(filePath);
        tx.beforeEditHashes().put(filePath.toString(), currentHash);
    }

    public void afterFileEdit(String txId, Path filePath) throws IOException {
        Transaction tx = activeTransactions.get(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        String newHash = ConflictDetector.computeFileHash(filePath);
        tx.currentHashes().put(filePath.toString(), newHash);
    }


    public void commitTransaction(String txId) throws IOException {
        Transaction tx = activeTransactions.remove(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        if (org.zfs.manager.ConflictDetector.hasConflicts(tx)) {
            rollbackTransaction(txId);
            throw new IllegalStateException("Konflikt erkannt, Rollback durchgeführt");
        }

        snapshotManager.deleteSnapshot(tx.snapshotName());
    }

    public void rollbackTransaction(String txId) {
        Transaction tx = activeTransactions.remove(txId);
        if (tx != null) snapshotManager.rollbackToSnapshot(tx.snapshotName());
    }
}
