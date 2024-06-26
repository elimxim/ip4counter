package com.github.elimxim.ip4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.elimxim.ip4.IpV4Support.*;

public class IPv4Counter {
    private static final int ARRAY_LENGTH = 536_870_912; // 2^29

    private final Path path;

    public IPv4Counter(Path path) {
        this.path = path;
    }

    public long countUnique() throws IOException {
        var counter = new AtomicLong();
        var dupCounter = new AtomicLong();
        byte[] array = new byte[ARRAY_LENGTH];
        try (var lines = Files.lines(path)) {
            lines.forEach(address -> {
                int ip = byteArrayToInt(strToByteArray(address));
                int result = array[ip >> 3] & (1 << (ip & 7));
                if (result == 0) {
                    array[ip >> 3] |= (1 << (ip & 7));
                } else {
                    dupCounter.getAndIncrement();
                }
                counter.getAndIncrement();
            });
        }
        return counter.get() - dupCounter.get();
    }
}
