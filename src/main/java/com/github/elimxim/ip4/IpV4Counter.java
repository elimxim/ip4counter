package com.github.elimxim.ip4;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

public class IpV4Counter {
    private final IpV4PartitionStorage partitionStorage;

    /**
     * Reading batch size.
     */
    private final int batchSize;

    /**
     * Maximum cache capacity.
     */
    private final int maxCacheSize;

    /**
     * Cache used to store ip addresses
     * from different partitions.
     */
    private final int[] cache;

    public IpV4Counter(IpV4PartitionStorage partitionStorage, int batchSize, int maxCacheSize) {
        this.partitionStorage = partitionStorage;
        this.batchSize = batchSize;
        this.maxCacheSize = maxCacheSize;
        this.cache = new int[maxCacheSize];
    }

    /**
     * Counts unique ip addresses in a file.
     *
     * @param path - file with ip addresses
     * @return number of unique ip addresses
     */
    public long countUnique(Path path) {
        long counter = 0;
        long dupCounter = 0;
        int cacheSize = 0;
        int currentPartition = -1;
        Set<Integer> adressSet = null;
        try (var in = IpV4InputStream.newInputStream(path, batchSize)) {
            int[] addresses;
            while ((addresses = in.read()).length != 0) {
                // first run
                if (currentPartition == -1) {
                    currentPartition = partitionStorage.calcPartition(addresses);
                    adressSet = partitionStorage.loadAddresses(currentPartition);
                }

                // cache threshold -> switching partitions
                if (cacheSize == maxCacheSize) {
                    currentPartition = partitionStorage.calcPartition(cache);
                    partitionStorage.storeAddresses(currentPartition, adressSet);
                    adressSet = partitionStorage.loadAddresses(currentPartition);
                }

                // ip address batch processing
                for (int address : addresses) {
                    int partition = partitionStorage.calcPartition(address);
                    if (partition == currentPartition) {
                        if (!adressSet.add(address)) {
                            dupCounter++;
                        }
                        counter++;
                    } else {
                        cache[cacheSize++] = address;
                    }
                }
            }
            return counter - dupCounter;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
