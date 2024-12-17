package com.menkaix.backlogs.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.io.ByteArrayInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class PlantUMLEncoder {

    public static String plantUMLEncode(String text) {

        try {

            // encode to UTF-8 and compress using Deflate algorithm

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(baos, new Deflater());
            dos.write(text.getBytes(StandardCharsets.UTF_8));
            dos.close();
            byte[] compressedData = baos.toByteArray();

            // encode using custom base64 encoding
            return customBase64Encode(compressedData);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/* YOUR_CHARSET? */)));
    }

    public static String toBase64(String arg) {
        return Base64.getEncoder().encodeToString(arg.getBytes(StandardCharsets.UTF_8));
    }

    public static String toURL(String arg) {
        try {
            return URLEncoder.encode(arg, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fonction pour compresser une chaîne en utilisant l'algorithme "Deflate"
    public static String compress(String arg) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new DeflaterOutputStream(baos, new Deflater());
            dos.write(arg.getBytes(StandardCharsets.UTF_8));
            dos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fonction pour décompresser une chaîne en utilisant l'algorithme "Deflate"
    public static String decompress(String compressedStr) {
        try {
            byte[] compressedBytes = Base64.getDecoder().decode(compressedStr);
            ByteArrayInputStream bais = new ByteArrayInputStream(compressedBytes);
            InflaterInputStream iis = new InflaterInputStream(bais, new Inflater());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = iis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fonction pour encoder du texte en PlantUML pour une URL
    public static String encodePlantUML(String text) {
        String compressed = compress(text);
        return toURL(compressed);
    }

    public static String encode64(String data) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < data.length(); i += 3) {
            if (i + 2 == data.length()) {
                r.append(append3bytes(data.charAt(i), data.charAt(i + 1), 0));
            } else if (i + 1 == data.length()) {
                r.append(append3bytes(data.charAt(i), 0, 0));
            } else {
                r.append(append3bytes(data.charAt(i), data.charAt(i + 1), data.charAt(i + 2)));
            }
        }
        return r.toString();
    }

    public static String customBase64Encode(String data) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < data.length(); i += 3) {
            if (i + 2 == data.length()) {
                r.append(append3bytes(data.charAt(i), data.charAt(i + 1), 0));
            } else if (i + 1 == data.length()) {
                r.append(append3bytes(data.charAt(i), 0, 0));
            } else {
                r.append(append3bytes(data.charAt(i), data.charAt(i + 1), data.charAt(i + 2)));
            }
        }
        return r.toString();
    }

    public static String customBase64Encode(byte[] data) {
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
