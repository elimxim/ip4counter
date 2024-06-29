package com.github.elimxim.ip4;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread statThread = new Thread(new StatPrinter());
        statThread.start();

        var result = new IPv4Counter().countUnique(Path.of(args[0]));

        statThread.interrupt();
        statThread.join();

        System.out.println();
        System.out.printf("==results==%n");
        System.out.printf("total: %d%n", result.totalNumber());
        System.out.printf("unique: %d%n", result.uniqueNumber());
        System.out.printf("dup: %d%n", result.dupNumber());

    }
}
