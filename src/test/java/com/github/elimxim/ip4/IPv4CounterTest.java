package com.github.elimxim.ip4;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IPv4CounterTest {
    private final IpAddressFileGenerator generator = new IpAddressFileGenerator();

    @TempDir(cleanup = CleanupMode.ALWAYS)
    private Path tempDir;

    @Test
    void testOne() throws IOException {
        Path file = Files.createFile(tempDir.resolve("ip4_test_1"));
        var counter = new IPv4Counter(file);

        int expected = generator.generate(file, 1, 0);
        assertEquals(expected, counter.countUnique());
    }

    @Test
    void testAllDuplicates() throws IOException {
        Path file = Files.createFile(tempDir.resolve("ip4_test_1"));
        var counter = new IPv4Counter(file);

        int expected = generator.generate(file, 10, 10);
        assertEquals(expected, counter.countUnique());
    }

    @Test
    void testMillion() throws IOException {
        Path file = Files.createFile(tempDir.resolve("ip4_test_1_000_000"));
        var counter = new IPv4Counter(file);

        int expected = generator.generate(file, 1_000_000, 0);
        assertEquals(expected, counter.countUnique());
    }

    @Test
    void testMillionAndHalfDupplicates() throws IOException {
        Path file = Files.createFile(tempDir.resolve("ip4_test_1_000_000"));
        var counter = new IPv4Counter(file);

        int expected = generator.generate(file, 1_000_000, 500_000);
        assertEquals(expected, counter.countUnique());
    }
}