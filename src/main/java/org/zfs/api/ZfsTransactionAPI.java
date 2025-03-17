package org.zfs.api;

import org.zfs.manager.TransactionManager;
import org.zfs.manager.ZfsSnapshotManager;

import java.io.IOException;
import java.nio.file.Path;

public class ZfsTransactionAPI {
    private final TransactionManager transactionManager;

    public ZfsTransactionAPI(String dataset) {
        ZfsSnapshotManager snapshotManager = new ZfsSnapshotManager(dataset);
        this.transactionManager = new TransactionManager(snapshotManager);
    }

    public String startTransaction() {
        return transactionManager.startTransaction();
    }

    public void commitTransaction(String transactionId) throws IOException {
        transactionManager.commitTransaction(transactionId);
    }

    public void rollbackTransaction(String transactionId) {
        transactionManager.rollbackTransaction(transactionId);
    }

    public void writeFile(TransactionManager transactionManager, String txId, Path path, String content) throws Exception {
        org.zfs.manager.FileOperationHandler.writeFile(transactionManager,txId, path, content);
    }

    public String readFile(Path path) throws Exception {
        return org.zfs.manager.FileOperationHandler.readFile(path);
    }

    public void deleteFile(TransactionManager transactionManager, String txId,Path path) throws Exception {
        org.zfs.manager.FileOperationHandler.deleteFile(transactionManager,txId,path);
    }
}
