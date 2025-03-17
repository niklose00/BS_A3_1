package org.zfs.manager;

import java.io.IOException;
import java.nio.file.*;

public class FileOperationHandler {

    public static String readFile(Path path) throws IOException {
        return Files.readString(path);
    }

    public static void writeFile(TransactionManager transactionManager,String txId, Path path, String content) throws IOException {
        // Vorher Hash speichern
        transactionManager.beforeFileEdit(txId, path);

        // Datei schreiben
        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Nachher neuen Hash speichern
        transactionManager.afterFileEdit(txId, path);
    }

    public static void deleteFile(TransactionManager transactionManager,String txId, Path path) throws IOException {
        // Vorher Hash speichern
        transactionManager.beforeFileEdit(txId, path);

        // Datei löschen
        Files.deleteIfExists(path);

        // Nachher Hash als leer setzen, da Datei nicht mehr existiert
        transactionManager.afterFileEdit(txId, path);
    }
}
