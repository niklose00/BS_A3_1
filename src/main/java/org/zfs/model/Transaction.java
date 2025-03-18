package org.zfs.model;

import java.nio.file.Path;

public class Transaction {

    private String id;
    private String snapshotName;
    private Path filePath;
    private String initialHash;
    private String beforeEditHash;
    private String currentHash;

    // Standard-Konstruktor (ohne Parameter)
    public Transaction() {
    }

    // Konstruktor mit allen Feldern
    public Transaction(String id,
                       String snapshotName,
                       Path filePath,
                       String initialHash,
                       String beforeEditHash,
                       String currentHash) {
        this.id = id;
        this.snapshotName = snapshotName;
        this.filePath = filePath;
        this.initialHash = initialHash;
        this.beforeEditHash = beforeEditHash;
        this.currentHash = currentHash;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSnapshotName() {
        return snapshotName;
    }

    public void setSnapshotName(String snapshotName) {
        this.snapshotName = snapshotName;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String getInitialHash() {
        return initialHash;
    }

    public void setInitialHash(String initialHash) {
        this.initialHash = initialHash;
    }

    public String getBeforeEditHash() {
        return beforeEditHash;
    }

    public void setBeforeEditHash(String beforeEditHash) {
        this.beforeEditHash = beforeEditHash;
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", snapshotName='" + snapshotName + '\'' +
                ", filePath=" + filePath +
                ", initialHash='" + initialHash + '\'' +
                ", beforeEditHash='" + beforeEditHash + '\'' +
                ", currentHash='" + currentHash + '\'' +
                '}';
    }
}
