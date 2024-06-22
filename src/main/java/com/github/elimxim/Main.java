package com.github.elimxim;

public class Main {
    public static void main(String[] args) {
        String ip = "145.67.23.4";
        var b1 = strToByteArray(ip);
        System.out.println(byteArrayToStr(b1));
        var i = byteArrayToInt(b1);
        System.out.println(i);
        var b2 = intToByteArray(i);
        System.out.println(byteArrayToStr(b2));
        var l = byteArrayToLong(b2);
        System.out.println(l);
    }

    static byte[] strToByteArray(String address) {
        String[] octets = address.split("\\.");
        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++) {
            result[i] = (byte) (Integer.parseInt(octets[i]));
        }
        return result;
    }

    static String byteArrayToStr(byte[] address) {
        StringBuilder builder = new StringBuilder();
        builder.append(address[0] & 0xFF);
        for (int i = 1; i < 4; i++) {
            builder.append(".");
            builder.append(address[i] & 0xFF);
        }
        return builder.toString();
    }

    static int byteArrayToInt(byte[] address) {
        int result = address[3] & 0xFF;
        result |= ((address[2] << 8) & 0xFF00);
        result |= ((address[1] << 16) & 0xFF0000);
        result |= ((address[0] << 24) & 0xFF000000);
        return result;
    }

    static long byteArrayToLong(byte[] address) {
        long result = address[3];
        result |= (long) (address[2] & 0xFF) << 8;
        result |= (long) (address[1] & 0xFF) << 16;
        result |= (long) (address[0] & 0xFF) << 24;
        return result;
    }

    static byte[] intToByteArray(int address) {
        byte[] result = new byte[4];
        result[0] = (byte) ((address >>> 24) & 0xFF);
        result[1] = (byte) ((address >>> 16) & 0xFF);
        result[2] = (byte) ((address >>> 8) & 0xFF);
        result[3] = (byte) (address & 0xFF);
        return result;
    }
}
