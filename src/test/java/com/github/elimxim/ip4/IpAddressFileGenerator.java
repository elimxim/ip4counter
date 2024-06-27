package com.github.elimxim.ip4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Random;

import static com.github.elimxim.ip4.IpV4Support.convertToByteArray;
import static com.github.elimxim.ip4.IpV4Support.convertToString;

public class IpAddressFileGenerator {
    public int generate(Path path, int number, int dupNumber) throws IOException {
        if (number < dupNumber) {
            throw new IllegalArgumentException("number < dupNumber");
        }

        var random = new Random();

        int uniqueNumber = number - dupNumber;
        if (number == dupNumber) {
            uniqueNumber = 1;
            dupNumber--;
        }

        var uniqueAddresses = new HashSet<Integer>(uniqueNumber);
        while (uniqueAddresses.size() != uniqueNumber) {
            uniqueAddresses.add(random.nextInt());
        }

        int[] addresses = new int[number];

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

        String[] srtAddresses = new String[addresses.length];
        for (int i = 0; i < srtAddresses.length; i++) {
            srtAddresses[i] = convertToString(convertToByteArray(addresses[i]));
        }

        try (var out = Files.newBufferedWriter(path)) {
            for (String strAddress : srtAddresses) {
                out.write(strAddress);
                out.newLine();
            }
        }

        return uniqueAddresses.size();
    }
}
