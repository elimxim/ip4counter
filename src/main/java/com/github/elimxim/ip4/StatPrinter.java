package com.github.elimxim.ip4;

public class StatPrinter implements Runnable {
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        long max = Runtime.getRuntime().maxMemory();
        long total = 0, free = 0, used = 0;
        long prevFree = 0, prevTotal = 0;
        long deltaUsed = 0, deltaFree = 0;

        print(elapsedTime, max, total, free, used, deltaFree, deltaUsed, true);
        while (!Thread.currentThread().isInterrupted()) {
            delay(800);
            total = Runtime.getRuntime().totalMemory();
            free = Runtime.getRuntime().freeMemory();
            used = total - free;
            deltaUsed = used - (prevTotal - prevFree);
            deltaFree = free - prevFree;
            elapsedTime = System.currentTimeMillis() - startTime;

            print(elapsedTime, max, total, free, used, deltaFree, deltaUsed, false);

            prevTotal = total;
            prevFree = free;
        }
    }

    private static void print(
            long elapsedTime,
            long max, long total,
            long free, long used,
            long deltaFree, long deltaUsed,
            boolean first
    ) {
        if (!first) {
            System.out.print(cursorUp(5));
        }

        System.out.printf(" time: %ds      %n", elapsedTime / 1000);
        System.out.printf("==memory==%n");
        if (max != Long.MAX_VALUE) {
            System.out.printf("total: %dMb  max: %dMb      %n", mb(total), mb(max));
        } else {
            System.out.printf("total: %dMb  max: N/A      %n", mb(total));
        }
        System.out.printf(" free: %dMb  %+dMb      %n", mb(free), mb(deltaFree));
        System.out.printf(" used: %dMb  %+dMb      %n", mb(used), mb(deltaUsed));
    }

    private static long mb(long byteNum) {
        return byteNum >> 20; // byteNum ÷ 1024 ÷ 1024
    }

    private static String cursorUp(int lines) {
        return (char) 0x1b + "[" + lines + "A\r";
    }

    private static void delay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
