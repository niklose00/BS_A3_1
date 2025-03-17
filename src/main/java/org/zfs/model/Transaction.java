package org.zfs.model;

import java.util.Map;

public record Transaction(
        String id,
        String snapshotName,
        Map<String, String> initialHashes,
        Map<String, String> beforeEditHashes,
        Map<String, String> currentHashes
) { }
