package com.github.elimxim.ip4;

public record CountingResult(long totalNumber, long dupNumber) {
    public long uniqueNumber() {
        return totalNumber - dupNumber;
    }
}
