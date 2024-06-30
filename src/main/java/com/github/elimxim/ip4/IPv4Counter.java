package com.github.elimxim.ip4;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.stream.Stream;

import static com.github.elimxim.ip4.IPv4Support.parseIpAddress;

/**
 * Implements the target solution using an array of long.
 */
public class IPv4Counter {
    /**
     * Bit array size = 2^26 for the long type.
     */
    private static final int ARRAY_LENGTH = 67_108_864;

    /**
     * Calculates {@link CountingResult}.
     * @param path - path to the file containing ip addresses
     * @return counting results
     */
    public CountingResult count(Path path) {
        checkPath(path);
        long[] counter = new long[1];
        long[] dupCounter = new long[1];
        long[] array = new long[ARRAY_LENGTH];
        try (var lines = lineStream(path)) {
            lines.forEach(address -> {
                int ip = parseIpAddress(address);
                if ((array[ip >>> 6] & (1L << (ip & 63))) == 0) {
                    array[ip >>> 6] |= (1L << (ip & 63));
                } else {
                    dupCounter[0]++;
                }
                counter[0]++;
            });
        }
        return new CountingResult(counter[0], dupCounter[0]);
    }

    private static void checkPath(Path path) {
        if (Files.notExists(path)) {
            throw new InvalidPathException(path.toString(), "path doesn't exist");
        } else if (Files.isDirectory(path)) {
            throw new InvalidPathException(path.toString(), "path is a directory");
        } else if (!Files.isReadable(path)) {
            throw new InvalidPathException(path.toString(), "path isn't readable");
        }
    }

    private static Stream<String> lineStream(Path path) {
        try {
            return Files.lines(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
