package org.zfs.manager;

import java.io.IOException;

public class ZfsSnapshotManager {
    private final String dataset;

    public ZfsSnapshotManager(String dataset) {
        this.dataset = dataset;
    }

    public String createSnapshot(String transactionId) {
        String snapshotName = dataset + "@tx_" + transactionId;
        executeCommand("zfs snapshot " + snapshotName);
        return snapshotName;
    }

    public void rollbackToSnapshot(String snapshotName) {
        executeCommand("zfs rollback " + snapshotName);
    }

    public void deleteSnapshot(String snapshotName) {
        executeCommand("zfs destroy " + snapshotName);
    }

    private void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Fehler bei ZFS-Befehl: " + command, e);
        }
    }
}
