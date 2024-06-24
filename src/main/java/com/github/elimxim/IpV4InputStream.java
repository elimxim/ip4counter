package com.github.elimxim;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class IpV4InputStream implements AutoCloseable {
    private final int batchSize;
    private final BufferedReader in;

    private IpV4InputStream(BufferedReader in, int batchSize) {
        this.batchSize = batchSize;
        this.in = in;
    }

    private int[] read() throws IOException {
        String line;
        int idx = 0;
        int[] addresses = new int[batchSize];
        while (idx < batchSize && (line = in.readLine()) != null) {
            addresses[idx++] = IpV4Support.byteArrayToInt(IpV4Support.strToByteArray(line));
        }

        if (idx != batchSize) {
            int[] tmp = addresses;
            addresses = new int[idx];
            System.arraycopy(tmp, 0, addresses, 0, idx);
        }

        return addresses;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }

    public static IpV4InputStream newInputStream(Path path, int batchSize) throws IOException {
        return new IpV4InputStream(Files.newBufferedReader(path), batchSize);
    }
}
