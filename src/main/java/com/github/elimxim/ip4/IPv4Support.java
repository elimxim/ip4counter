package com.github.elimxim.ip4;

public class IPv4Support {
    private static final String OCTET_SEPARATOR = ".";
    private static final String OCTET_REGEX = "\\.";
    private static final int ADDRESS_SIZE = 4;

    public static int parseIpAddress(String address) {
        String[] octets = address.split(OCTET_REGEX);
        if (octets.length != ADDRESS_SIZE) {
            throw new InvalidIpAddress(address);
        }

        int result = Integer.parseInt(octets[3]) & 0xFF;
        result |= (Integer.parseInt(octets[2]) << 8) & 0xFF00;
        result |= (Integer.parseInt(octets[1]) << 16) & 0xFF0000;
        result |= (Integer.parseInt(octets[0]) << 24) & 0xFF000000;
        return result;
    }

    public static byte[] convertToByteArray(int address) {
        byte[] array = new byte[ADDRESS_SIZE];
        array[0] = (byte) ((address >>> 24) & 0xFF);
        array[1] = (byte) ((address >>> 16) & 0xFF);
        array[2] = (byte) ((address >>> 8) & 0xFF);
        array[3] = (byte) (address & 0xFF);
        return array;
    }

    public static int convertToInt(byte[] address) {
        int result = address[3] & 0xFF;
        result |= (address[2] << 8) & 0xFF00;
        result |= (address[1] << 16) & 0xFF0000;
        result |= (address[0] << 24) & 0xFF000000;
        return result;
    }

    public static String convertToString(byte[] address) {
        StringBuilder builder = new StringBuilder();
        builder.append(address[0] & 0xFF);
        for (int i = 1; i < ADDRESS_SIZE; i++) {
            builder.append(OCTET_SEPARATOR);
            builder.append(address[i] & 0xFF);
        }
        return builder.toString();
    }

    public static long convertToLong(byte[] address) {
        long result = address[3];
        result |= (long) (address[2] & 0xFF) << 8;
        result |= (long) (address[1] & 0xFF) << 16;
        result |= (long) (address[0] & 0xFF) << 24;
        return result;
    }
}
