package com.github.elimxim.ip4;

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
        fillByteArray(result, 0, address);
        return result;
    }

    public static void fillByteArray(byte[] array, int pos, int address) {
        array[pos] = (byte) ((address >>> 24) & 0xFF);
        array[pos + 1] = (byte) ((address >>> 16) & 0xFF);
        array[pos + 2] = (byte) ((address >>> 8) & 0xFF);
        array[pos + 3] = (byte) (address & 0xFF);
    }

    public static String byteArrayToStr(byte[] address) {
        StringBuilder builder = new StringBuilder();
        builder.append(address[0] & 0xFF);
        for (int i = 1; i < ADDRESS_SIZE; i++) {
            builder.append(OCTET_SEPARATOR);
            builder.append(address[i] & 0xFF);
        }
        return builder.toString();
    }

    public static int byteArrayToInt(byte[] address) {
        return bytesToInt(address[0], address[1], address[2], address[3]);
    }

    public static int bytesToInt(byte o1, byte o2, byte o3, byte o4) {
        int result = o4 & 0xFF;
        result |= (o3 << 8) & 0xFF00;
        result |= (o2 << 16) & 0xFF0000;
        result |= (o1 << 24) & 0xFF000000;
        return result;
    }

    public static long byteArrayToLong(byte[] address) {
        long result = address[3];
        result |= (long) (address[2] & 0xFF) << 8;
        result |= (long) (address[1] & 0xFF) << 16;
        result |= (long) (address[0] & 0xFF) << 24;
        return result;
    }

    public static int firstOctet(int address) {
        return (address >>> 24) & 0xFF;
    }
}
