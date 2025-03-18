package org.zfs;

import org.zfs.manager.FileOperationHandler;
import org.zfs.manager.TransactionManager;
import org.zfs.manager.ZfsSnapshotManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) {
        try {
            // Erstelle eine temporäre Datei und schreibe den Initialinhalt
            Path tempFile = Files.createTempFile("conflictTest", ".txt");
            Files.writeString(tempFile, "Original Content", StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("Temp file created: " + tempFile);
            System.out.println("Initial file content: " + Files.readString(tempFile));

            // Erstelle Snapshot-Manager und Transaction-Manager (hier mit einem Dummy-Dataset "tank/mydataset")
            ZfsSnapshotManager snapshotManager = new ZfsSnapshotManager("tank/mydataset");
            TransactionManager transactionManager = new TransactionManager(snapshotManager);

            // Person A startet eine Transaktion auf der Datei
            String txIdA = transactionManager.startTransaction(tempFile);
            System.out.println("Person A started transaction: " + txIdA);

            // Person B startet ebenfalls eine Transaktion auf derselben Datei
            String txIdB = transactionManager.startTransaction(tempFile);
            System.out.println("Person B started transaction: " + txIdB);

            // Person A ändert den Dateiinhalt und committet
            try {
                FileOperationHandler.writeFile(transactionManager, txIdA, "Person A's new content");
                System.out.println("Person A committed transaction successfully.");
            } catch (Exception e) {
                System.out.println("Person A encountered an error: " + e.getMessage());
            }

            // Person B versucht danach die Datei zu ändern und zu committen.
            // Da die Datei bereits von Person A geändert wurde, kommt es hier zu einem Konflikt.
            try {
                FileOperationHandler.writeFile(transactionManager, txIdB, "Person B's new content");
                System.out.println("Person B committed transaction successfully.");
            } catch (Exception e) {
                System.out.println("Person B encountered a conflict: " + e.getMessage());
            }

            // Ausgabe des finalen Datei-Inhalts
            System.out.println("Final file content: " + Files.readString(tempFile));

            // Aufräumen: Lösche die temporäre Datei
            Files.deleteIfExists(tempFile);
            System.out.println("Temporary file deleted.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
