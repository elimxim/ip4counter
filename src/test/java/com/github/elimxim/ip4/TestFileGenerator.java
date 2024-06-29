package com.github.elimxim.ip4;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;

import static com.github.elimxim.ip4.IPv4Support.convertToByteArray;
import static com.github.elimxim.ip4.IPv4Support.convertToString;

public class TestFileGenerator {
    private final Random random = new Random();

    public int generate(Path path, int totalNumber, int dupNumber) {
        assert dupNumber < totalNumber;

        int uniqueNumber = totalNumber - dupNumber;

        var uniqueAddresses = new HashSet<Integer>(uniqueNumber);
        while (uniqueAddresses.size() != uniqueNumber) {
            uniqueAddresses.add(random.nextInt());
        }

        int[] addresses = new int[totalNumber];

        int idx = 0;
        for (Integer uniqueAddress : uniqueAddresses) {
            addresses[idx++] = uniqueAddress;
        }

        for (int i = 0; i < dupNumber; i++) {
            addresses[i + uniqueNumber] = addresses[random.nextInt(uniqueNumber)];
        }

        for (int i = addresses.length - 1; i >= 0; i--) {
            int j = random.nextInt(i + 1);
            int tmp = addresses[i];
            addresses[i] = addresses[j];
            addresses[j] = tmp;
        }

        try (var out = Files.newBufferedWriter(path)) {
            for (int address : addresses) {
                out.write(convertToString(convertToByteArray(address)));
                out.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return uniqueNumber;
    }
}
