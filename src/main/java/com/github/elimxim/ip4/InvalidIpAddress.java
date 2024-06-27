package com.github.elimxim.ip4;

public class InvalidIpAddress extends RuntimeException {
    public InvalidIpAddress(String address) {
        super("ip address '" + address + "' is invalid");
    }
}
