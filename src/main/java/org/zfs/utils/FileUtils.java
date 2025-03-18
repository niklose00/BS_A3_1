package org.zfs.utils;

import java.io.IOException;
import java.nio.file.*;

public class FileUtils {

    private FileUtils() {} // Verhindert Instanziierung

    public static void ensureDirectoryExists(Path dir) {
        try {
            if (Files.notExists(dir)) {
                Files.createDirectories(dir);
                System.out.println("Verzeichnis erstellt: " + dir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Erstellen des Verzeichnisses: " + dir, e);
        }
    }

    public static boolean fileExists(Path file) {
        return Files.exists(file) && Files.isRegularFile(file);
    }

}
