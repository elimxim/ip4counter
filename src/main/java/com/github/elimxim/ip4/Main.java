package com.github.elimxim.ip4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        var path = Path.of(args[0]);
        checkPath(path);

        var counter = new IPv4Counter(path);
        System.out.println(counter.countUnique());
    }

    private static void checkPath(Path path) {
        if (Files.notExists(path)) {
            throw new RuntimeException("path '" + path + "' doesn't exist");
        } else if (Files.isDirectory(path)) {
            throw new RuntimeException("path '" + path + "' is a directory");
        } else if (!Files.isReadable(path)) {
            throw new RuntimeException("path '" + path + "' isn't readable");
        }
    }
}
