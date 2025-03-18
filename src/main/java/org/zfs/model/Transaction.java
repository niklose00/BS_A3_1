package org.zfs.model;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

public class Transaction {
    public final String id;
    public final String snapshotName;
    public final Map<Path, String> initialFileHashes;
    public final Set<Path> changedFiles;

    public Transaction(String id, String snapshotName, Map<Path, String> initialFileHashes, Set<Path> changedFiles) {
        this.id = id;
        this.snapshotName = snapshotName;
        this.initialFileHashes = initialFileHashes;
        this.changedFiles = changedFiles;
    }
}
