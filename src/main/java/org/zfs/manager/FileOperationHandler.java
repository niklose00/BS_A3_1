package org.zfs.manager;

import java.io.IOException;
import java.nio.file.*;

public class FileOperationHandler {
    public static void writeFile(TransactionManager transactionManager, String txId, Path path, String content) throws IOException {
        writeFile(transactionManager, txId, path, content, false); // append = false
    }


    public static void writeFile(TransactionManager transactionManager, String txId, Path path, String content, boolean append) throws IOException {
        transactionManager.markFileChanged(txId, path);

        OpenOption[] options = append
                ? new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND}
                : new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING};

        Files.writeString(path, content, options);
    }

    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static void deleteFile(TransactionManager transactionManager, String txId, Path path) throws IOException {
        transactionManager.markFileChanged(txId, path);
        Files.deleteIfExists(path);
    }
}
