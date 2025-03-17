package org.zfs;

import org.zfs.manager.*;
import org.zfs.model.Transaction;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String dataset = "zfs_tx_pool/data";
        ZfsSnapshotManager snapshotManager = new ZfsSnapshotManager(dataset);
        TransactionManager transactionManager = new TransactionManager(snapshotManager);

        Path filePath = Path.of("/mnt/zfs/testfile.txt");

        // Initiale Datei schreiben
        Files.writeString(filePath, "Initialer String",StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Starte zwei parallele Transaktionen
        Future<String> future1 = executor.submit(() -> executeTransaction(transactionManager, filePath, "Änderung von T1"));

        Future<String> future2 = executor.submit(() -> executeTransaction(transactionManager, filePath, "Änderung von T2"));

        // Ergebnisse sammeln
        String result1 = future1.get();
        String result2 = future2.get();

        executor.shutdown();

        // Endergebnisse ausgeben
        System.out.println("Ergebnis Transaktion 1: " + result1);
        System.out.println("Ergebnis Transaktion 2: " + result2);
    }

    private static String executeTransaction(TransactionManager transactionManager, Path filePath, String newContent) {
        String txId = transactionManager.startTransaction();
        System.out.println("Transaktion gestartet: " + txId);

        try {
            // Datei bearbeiten
            FileOperationHandler.writeFile(transactionManager, txId, filePath, newContent);
            Thread.sleep(1000); // Simuliert Verzögerung

            // Transaktion committen
            transactionManager.commitTransaction(txId);
            return "Erfolgreich";
        } catch (IllegalStateException | InterruptedException e) {
            transactionManager.rollbackTransaction(txId);
            return "Fehlgeschlagen (Rollback ausgeführt)";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

