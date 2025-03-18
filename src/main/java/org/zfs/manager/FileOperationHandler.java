package org.zfs.manager;

import java.io.IOException;
import java.nio.file.*;

public class FileOperationHandler {

    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static void writeFile(TransactionManager transactionManager,String txId, String content) throws IOException {
        writeFile(transactionManager, txId, content, false);
    }

    public static void writeFile(TransactionManager transactionManager,String txId, String content, boolean append) throws IOException {
        Path filePath = transactionManager.getTransaction(txId).getFilePath();

        transactionManager.beforeFileEdit(txId, filePath);

        OpenOption[] options = append
                ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

        Files.writeString(filePath, content, options);

        transactionManager.afterFileEdit(txId, filePath);

        transactionManager.commitTransaction(txId);
    }

    public static void deleteFile(TransactionManager transactionManager,String txId) throws IOException {
        Path filePath = transactionManager.getTransaction(txId).getFilePath();

        transactionManager.beforeFileEdit(txId, filePath);

        Files.deleteIfExists(filePath);

        transactionManager.afterFileEdit(txId, filePath);

        transactionManager.commitTransaction(txId);

    }
}