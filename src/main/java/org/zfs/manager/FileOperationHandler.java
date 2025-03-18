package org.zfs.manager;

import java.io.IOException;
import java.nio.file.*;

public class FileOperationHandler {

    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static void writeFile(TransactionManager transactionManager,String txId, String content, boolean append) throws IOException {
        writeFile(transactionManager, txId, content, append);
    }

    public static void writeFile(TransactionManager transactionManager,String txId, String content) throws IOException {
        Path filePath = transactionManager.getTransaction(txId).getFilePath();

        // Vorher Hash speichern
        transactionManager.beforeFileEdit(txId, filePath);

        // Datei schreiben
        Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Nachher neuen Hash speichern
        transactionManager.afterFileEdit(txId, filePath);

        // Transaktion beenden
        transactionManager.commitTransaction(txId);
    }

    public static void deleteFile(TransactionManager transactionManager,String txId) throws IOException {
        Path filePath = transactionManager.getTransaction(txId).getFilePath();

        // Vorher Hash speichern
        transactionManager.beforeFileEdit(txId, filePath);

        // Datei löschen
        Files.deleteIfExists(filePath);

        // Nachher Hash als leer setzen, da Datei nicht mehr existiert
        transactionManager.afterFileEdit(txId, filePath);

        // Transaktion beenden
        transactionManager.commitTransaction(txId);

    }
}