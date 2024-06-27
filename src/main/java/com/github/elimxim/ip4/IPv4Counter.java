package com.github.elimxim.ip4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.github.elimxim.ip4.IpV4Support.parseIpAddress;

/**
 * Implements the target solution using an array of bytes.
 */
public class IPv4Counter {
    private static final int ARRAY_LENGTH = 536_870_912; // 2^29 for the array of bytes

    private final Path path;

    public IPv4Counter(Path path) {
        this.path = path;
    }

    public long countUnique() throws IOException {
        long[] counter = new long[1];
        long[] dupCounter = new long[1];
        byte[] array = new byte[ARRAY_LENGTH];
        try (var lines = Files.lines(path)) {
            lines.forEach(address -> {
                int ip = parseIpAddress(address);
                int result = array[ip >>> 3] & (1 << (ip & 7));
                if (result == 0) {
                    array[ip >>> 3] |= (1 << (ip & 7));
                } else {
                    dupCounter[0]++;
                }
                counter[0]++;
            });
        }
        return counter[0] - dupCounter[0];
    }
}
