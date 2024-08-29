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

public class PlantUMLEncoder {

    private static String order = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";

    private static String encode_(byte[] input) {
        String ans = "";

        char[] orderChar = order.toCharArray();

        for (int i = 0; i < input.length; i++) {
            ans += orderChar[input[i] & 0xFF];
        }

        return ans;
    }

    public static String encodePlant(String text) {
        byte[] zipped;
        try {
            zipped = deflate(text);
            return encode(zipped);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "err";
        }

    }

    private static byte[] deflate(String source) {
        Deflater deflater = new Deflater();

        deflater.setLevel(Deflater.DEFLATED);
        deflater.setStrategy(Deflater.HUFFMAN_ONLY);

        try {

            String urlEncodedSource = URLEncoder.encode(source, StandardCharsets.UTF_8.toString());
            deflater.setInput(urlEncodedSource.getBytes(StandardCharsets.UTF_8.toString()));

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
            return null;
        }
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // byte[] buffer = new byte[1024];
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // byte[] buffer = byteBuffer.array();

        while (!deflater.finished()) {

            int compressedSize = deflater.deflate(byteBuffer, Deflater.FULL_FLUSH);
            outputStream.write(byteBuffer.array(), 0, compressedSize);
        }

        deflater.end();

        byte[] zippedStringBytes = outputStream.toByteArray();

        return zippedStringBytes;
    }

    private static String encode(byte[] data) {
        String r = "";

        for (int i = 0; i < data.length; i += 3) {
            if (i + 2 == data.length) {
                r += append3bytes(data[i], data[i + 1], (byte) 0);
            } else if (i + 1 == data.length) {
                r += append3bytes(data[i], (byte) 0, (byte) 0);
            } else {
                r += append3bytes(data[i], data[i + 1], data[i + 2]);
            }
        }
        return r;
    }
    // 11111111 22222222 33333333
    // 111111 112222 222233 333333

    private static String append3bytes(byte b1, byte b2, byte b3) {
        byte c1 = (byte) (b1 >> 2);
        byte c2 = (byte) (((b1 & 0x3) << 4) | (b2 >> 4));
        byte c3 = (byte) (((b2 & 0xF) << 2) | (b3 >> 6));
        byte c4 = (byte) (b3 & 0x3F);
        String r = "";
        r += encode6bit((byte) (c1 & 0x3F));
        r += encode6bit((byte) (c2 & 0x3F));
        r += encode6bit((byte) (c3 & 0x3F));
        r += encode6bit((byte) (c4 & 0x3F));

        return r;
    }

    private static String encode6bit_(byte input) {
        String ans = "";

        char[] orderChar = order.toCharArray();

        ans += orderChar[input & 0x3F];
        
        return ans;
    }

    private static String encode6bit(byte b) {
        if (b < 10) {
            return Character.toString(48 + b);
        }

        b -= 10;
        if (b < 26) {
            return Character.toString(65 + b);
        }

        b -= 26;
        if (b < 26) {
            return Character.toString(97 + b);
        }

        b -= 26;
        if (b == 0) {
            return "-";
        }
        if (b == 1) {
            return "_";
        }
        return "?";
    }

    private static byte[] deflate_(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream,
                new Deflater(Deflater.BEST_COMPRESSION))) {
            deflaterOutputStream.write(data);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static String toHex(String arg) {
        return String.format("%040x", new BigInteger(1, arg.getBytes(/*YOUR_CHARSET?*/)));
    }

}
