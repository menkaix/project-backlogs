package com.menkaix.backlogs.utilities;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PlantUMLEncoder {

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
    }

    public static String plantUMLEncode(String text) {

        byte[] data = text.getBytes(StandardCharsets.UTF_8);

        return null;
    }

    public static String customBase64EncodeString(String data) {
        return customBase64EncodeBytes(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String customBase64EncodeBytes(byte[] data) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < data.length; i += 3) {
            if (i + 2 == data.length) {
                r.append(append3bytes(data[i], data[i + 1], 0));
            } else if (i + 1 == data.length) {
                r.append(append3bytes(data[i], 0, 0));
            } else {
                r.append(append3bytes(data[i], data[i + 1], data[i + 2]));
            }
        }
        return r.toString();
    }

    public static String encodeToBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static String append3bytes(int b1, int b2, int b3) {
        int c1 = b1 >> 2;
        int c2 = ((b1 & 0x3) << 4) | (b2 >> 4);
        int c3 = ((b2 & 0xF) << 2) | (b3 >> 6);
        int c4 = b3 & 0x3F;
        StringBuilder r = new StringBuilder();
        r.append(encode6bit(c1 & 0x3F));
        r.append(encode6bit(c2 & 0x3F));
        r.append(encode6bit(c3 & 0x3F));
        r.append(encode6bit(c4 & 0x3F));
        return r.toString();
    }

    private static char encode6bit(int b) {
        if (b < 10) {
            return (char) ('0' + b);
        }
        b -= 10;
        if (b < 26) {
            return (char) ('A' + b);
        }
        b -= 26;
        if (b < 26) {
            return (char) ('a' + b);
        }
        b -= 26;
        if (b == 0) {
            return '-';
        }
        if (b == 1) {
            return '_';
        }
        return '?';
    }

    public static String decode64(String data) {
        StringBuilder r = new StringBuilder();
        int i = 0;
        while (i < data.length()) {
            int c1 = decode6bit(data.charAt(i));
            i++;
            int c2 = decode6bit(data.charAt(i));
            i++;
            r.append((char) ((c1 << 2) | ((c2 & 0x30) >> 4)));
            if (i < data.length()) {
                int c3 = decode6bit(data.charAt(i));
                i++;
                r.append((char) (((c2 & 0xF) << 4) | ((c3 & 0x3C) >> 2)));
                if (i < data.length()) {
                    int c4 = decode6bit(data.charAt(i));
                    i++;
                    r.append((char) (((c3 & 0x3) << 6) | c4));
                }
            }
        }
        return r.toString();
    }

    private static int decode6bit(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'Z') {
            return c - 65 + 10;
        }
        if (c >= 'a' && c <= 'z') {
            return c - 97 + 36;
        }
        if (c == '-') {
            return 62;
        }
        if (c == '_') {
            return 63;
        }
        return 0;
    }

}
