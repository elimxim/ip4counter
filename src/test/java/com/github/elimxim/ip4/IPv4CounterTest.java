package com.github.elimxim.ip4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IPv4CounterTest {
    private final TestFileGenerator fileGenerator = new TestFileGenerator();
    private final IPv4Counter counter = new IPv4Counter();

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private Path tempDir;

    @Test
    void testOne() {
        Path file = file("ip4_test_1");
        int expected = fileGenerator.generate(file, 1, 0);
        assertEquals(expected, counter.countUnique(file).uniqueNumber());
    }

    @Test
    void testAllDuplicates() {
        Path file = file("ip4_test_1");
        int expected = fileGenerator.generate(file, 10, 9);
        assertEquals(expected, counter.countUnique(file).uniqueNumber());
    }

    @Test
    void testMillion() {
        Path file = file("ip4_test_1_000_000");
        int expected = fileGenerator.generate(file, 1_000_000, 0);
        assertEquals(expected, counter.countUnique(file).uniqueNumber());
    }

    @Test
    void testMillionAndHalfDuplicates() {
        Path file = file("ip4_test_500_000");
        int expected = fileGenerator.generate(file, 1_000_000, 500_000);
        assertEquals(expected, counter.countUnique(file).uniqueNumber());
    }

    private Path file(String name) {
        try {
            return Files.createFile(tempDir.resolve(name));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}