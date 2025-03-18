package org.zfs;

import org.zfs.api.ZfsTransactionAPI;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws Exception {
        ZfsTransactionAPI api = new ZfsTransactionAPI("zfs_tx_pool/data");
        Path filePath = Path.of("/mnt/zfs/test.txt");

        // ✅ Transaktion A starten
        String txA = api.startTransaction();
        System.out.println("🚀 Transaktion A gestartet: " + txA);

        // ✅ Transaktion B starten (parallel)
        String txB = api.startTransaction();
        System.out.println("🚀 Transaktion B gestartet: " + txB);

        // ✏️ Transaktion A ändert die Datei
        api.writeFile(txA, filePath, "Änderung durch A\n", false);
        System.out.println("✏️ Transaktion A schreibt...");

        // ✅ Transaktion A committet (ohne Konflikt)
        api.commitTransaction(txA);
        System.out.println("✅ Transaktion A erfolgreich committed.");

        // ✏️ Transaktion B versucht ebenfalls zu schreiben (kennt die Änderung von A nicht)
        api.writeFile(txB, filePath, "Änderung durch B\n", false);
        System.out.println("✏️ Transaktion B schreibt...");

        // ⚠️ Transaktion B versucht zu committen → Muss scheitern!
        try {
            api.commitTransaction(txB);
            System.out.println("✅ Transaktion B committed (Fehler! Hätte Konflikt sein müssen)");
        } catch (IllegalStateException e) {
            System.out.println("❌ Transaktion B fehlgeschlagen (Konflikt erkannt): " + e.getMessage());
        }
    }
}
