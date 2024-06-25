package com.github.elimxim.ip4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final int PARTITION_SIZE = 4;
    private static final int BATCH_SIZE = 1024;
    private static final int CACHE_SIZE = 1024 * 1024;

    public static void main(String[] args) {
        Path tmpRoot = Path.of(System.getProperty("java.io.tmpdir"));
        Path storagePath = tmpRoot.resolve("ip4partitions");
        IpV4PartitionStorage partitionStorage = null;
        try {
            if (Files.notExists(storagePath)) {
                Files.createDirectory(storagePath);
            }

            partitionStorage = new IpV4PartitionStorage(storagePath, PARTITION_SIZE);
            IpV4Counter counter = new IpV4Counter(partitionStorage, BATCH_SIZE, CACHE_SIZE);
            long addresses = counter.countUnique(Path.of(args[0]));
            System.out.println(addresses);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (partitionStorage != null) {
                partitionStorage.removeAll();
            }
        }
    }
}
