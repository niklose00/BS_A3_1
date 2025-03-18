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

    public String startTransaction(Path filePath) {
        String txId = UUID.randomUUID().toString();
        String snapshot = snapshotManager.createSnapshot(txId);

        // Datei-Hashes sammeln
        String initialHash = "";
        String beforeEditHash = "";
        String currentHash = "";

        try {
            initialHash = ConflictDetector.computeFileHash(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim erstellen des initialen Hashes", e);
        }

        activeTransactions.put(txId, new Transaction(txId, snapshot, filePath, initialHash, beforeEditHash, currentHash));

        return txId;
    }

    public void beforeFileEdit(String txId, Path filePath) throws IOException {
        Transaction tx = activeTransactions.get(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        String beforeEditHash = ConflictDetector.computeFileHash(filePath);
        tx.setBeforeEditHash(beforeEditHash);
    }

    public void afterFileEdit(String txId, Path filePath) throws IOException {
        Transaction tx = activeTransactions.get(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        String newHash = ConflictDetector.computeFileHash(filePath);
        tx.setCurrentHash(newHash);
    }


    public void commitTransaction(String txId) throws IOException {
        Transaction tx = activeTransactions.remove(txId);
        if (tx == null) throw new IllegalStateException("Transaktion nicht gefunden");

        if (org.zfs.manager.ConflictDetector.hasConflicts(tx)) {
            rollbackTransaction(txId);
            throw new IllegalStateException("Konflikt erkannt, Rollback durchgeführt");
        }

        snapshotManager.deleteSnapshot(tx.getSnapshotName());
    }

    public void rollbackTransaction(String txId) {
        Transaction tx = activeTransactions.remove(txId);
        if (tx != null) snapshotManager.rollbackToSnapshot(tx.getSnapshotName());
    }

    public Transaction getTransaction(String txId) {
        Transaction tx = activeTransactions.get(txId);
        if (tx == null) {
            throw new IllegalStateException("Transaktion nicht gefunden: " + txId);
        }
        return tx;
    }
}