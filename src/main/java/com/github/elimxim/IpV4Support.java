package com.github.elimxim;

public class IpV4Support {
    public static final String OCTET_SEPARATOR = "\\.";
    public static final int ADDRESS_SIZE = 4;

    public static byte[] strToByteArray(String address) {
        String[] octets = address.split(OCTET_SEPARATOR);
        if (octets.length != ADDRESS_SIZE) {
            throw new RuntimeException("invalid ip address: " + address);
        }

        byte[] result = new byte[ADDRESS_SIZE];
        for (int i = 0; i < ADDRESS_SIZE; i++) {
            result[i] = (byte) (Integer.parseInt(octets[i]));
        }
        return result;
    }

    public static byte[] intToByteArray(int address) {
        byte[] result = new byte[ADDRESS_SIZE];
        result[0] = (byte) ((address >>> 24) & 0xFF);
        result[1] = (byte) ((address >>> 16) & 0xFF);
        result[2] = (byte) ((address >>> 8) & 0xFF);
        result[3] = (byte) (address & 0xFF);
        return result;
    }

    public static String byteArrayToStr(byte[] address) {
        StringBuilder builder = new StringBuilder();
        builder.append(address[0] & 0xFF);
        for (int i = 1; i < ADDRESS_SIZE; i++) {
            builder.append(".");
            builder.append(address[i] & 0xFF);
        }
        return builder.toString();
    }

    public static int byteArrayToInt(byte[] address) {
        int result = address[3] & 0xFF;
        result |= ((address[2] << 8) & 0xFF00);
        result |= ((address[1] << 16) & 0xFF0000);
        result |= ((address[0] << 24) & 0xFF000000);
        return result;
    }

    public static long byteArrayToLong(byte[] address) {
        long result = address[3];
        result |= (long) (address[2] & 0xFF) << 8;
        result |= (long) (address[1] & 0xFF) << 16;
        result |= (long) (address[0] & 0xFF) << 24;
        return result;
    }
}
