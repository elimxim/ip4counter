package com.github.elimxim.ip4;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class IpV4PartitionStorage {
    private static final String FILE_PREFIX = "partition";
    private static final String FILE_EXT = "bin";
    private static final int BUFFER_SIZE = 1024 * 8; // 8kb

    private final Path storagePath;
    private final int partitionSize;

    public IpV4PartitionStorage(Path storagePath, int partitionSize) {
        this.storagePath = storagePath;
        this.partitionSize = partitionSize;
    }

    public int calcPartition(int address) {
        return IpV4Support.firstOctet(address) & (partitionSize - 1);
    }

    public int calcPartition(int[] addresses) {
        int[] counters = new int[partitionSize];
        for (int address : addresses) {
            int p = calcPartition(address);
            counters[p]++;
        }

        int max = counters[0];
        for (int i = 1; i < partitionSize; i++) {
            if (counters[i] > max) {
                max = counters[i];
            }
        }

        return max;
    }

    public Set<Integer> loadAddresses(int partition) {
        Path file = partitionFile(partition);
        if (!Files.exists(file)) {
            return new HashSet<>();
        } else {
            return loadPartition(file);
        }
    }

    private Set<Integer> loadPartition(Path file) {
        try (var in = new BufferedInputStream(Files.newInputStream(file))) {
            byte[] sizeBuffer = new byte[4];
            int readBytes;
            if ((readBytes = in.read(sizeBuffer)) != -1) {
                if (readBytes != 4) {
                    throw new RuntimeException("file '" + file + "' is corrupted");
                }

                int setSize = ByteBuffer.wrap(sizeBuffer).getInt();
                Set<Integer> set = new HashSet<>(setSize * 2); // to avoid a sooner rehash
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((readBytes = in.read(buffer)) != -1) {
                    if ((readBytes & 1) != 0) { // = mod 2
                        throw new RuntimeException("file '" + file + "' is corrupted");
                    }

                    if (readBytes != BUFFER_SIZE) {
                        byte[] tmp = buffer;
                        buffer = new byte[readBytes];
                        System.arraycopy(tmp, 0, buffer, 0, readBytes);
                    }

                    for (int i = 0; i < buffer.length; i += 4) {
                        set.add(IpV4Support.bytesToInt(buffer[i], buffer[i + 1], buffer[i + 2], buffer[i + 3]));
                    }
                }
                return set;
            } else {
                return new HashSet<>();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeAddresses(int partition, Set<Integer> set) {
        if (set.size() == 0) {
            throw new RuntimeException("attempt to store an empty partition " + partition);
        }

        Path file = partitionFile(partition);
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        storePartition(file, set);
    }

    private void storePartition(Path file, Set<Integer> set) {
        try (var out = new BufferedOutputStream(Files.newOutputStream(file))) {
            byte[] sizeBuffer = ByteBuffer.allocate(4).putInt(set.size()).array();
            out.write(sizeBuffer);

            int idx = 0;
            byte[] buffer = new byte[BUFFER_SIZE];
            for (Integer address : set) {
                if (idx == BUFFER_SIZE) {
                    out.write(buffer);
                    idx = 0;
                }

                IpV4Support.fillByteArray(buffer, idx, address);
                idx += 4;
            }

            if (idx != 0) {
                byte[] last = new byte[idx];
                System.arraycopy(buffer, 0, last, 0, idx);
                out.write(last);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeAll() {
        for (int p = 0; p < partitionSize; p++) {
            Path file = partitionFile(p);
            if (Files.exists(file)) {
                try {
                    Files.delete(file);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private Path partitionFile(int partition) {
        return storagePath.resolve(FILE_PREFIX + partition + "." + FILE_EXT);
    }
}
