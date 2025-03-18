package org.zfs.api;

import org.zfs.manager.FileOperationHandler;
import org.zfs.manager.TransactionManager;
import org.zfs.manager.ZfsSnapshotManager;
import org.zfs.model.Transaction;

import java.io.IOException;
import java.nio.file.Path;

public class ZfsTransactionAPI {
    private final TransactionManager transactionManager;

    public ZfsTransactionAPI(String dataset) {
        ZfsSnapshotManager snapshotManager = new ZfsSnapshotManager(dataset);
        this.transactionManager = new TransactionManager(snapshotManager);
    }

    // ✅ Transaktion starten
    public String startTransaction() {
        return transactionManager.startTransaction();
    }

    // ✅ Transaktion committen
    public void commitTransaction(String transactionId) throws IOException {
        transactionManager.commitTransaction(transactionId);
    }

    // ✅ Transaktion zurückrollen
    public void rollbackTransaction(Transaction tx) {
        transactionManager.rollbackTransaction(tx);
    }

    // ✅ Datei schreiben (internes Tracking der Änderung wird erledigt)
    public void writeFile(String transactionId, Path path, String content, boolean append) throws IOException {
        FileOperationHandler.writeFile(transactionManager, transactionId, path, content, append);
    }

    // ✅ Datei lesen (kein Transaktionskontext nötig)
    public String readFile(Path path) throws IOException {
        return FileOperationHandler.readFile(path);
    }

    // ✅ Datei löschen (tracked Änderung automatisch)
    public void deleteFile(String transactionId, Path path) throws IOException {
        FileOperationHandler.deleteFile(transactionManager, transactionId, path);
    }
}
