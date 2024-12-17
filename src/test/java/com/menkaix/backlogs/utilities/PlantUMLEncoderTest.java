package com.menkaix.backlogs.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlantUMLEncoderTest {

    @Test
    public void testToHex() {
        String input = "test";
        String expected = "0000000000000000000000000000000074657374";
        assertEquals(expected, PlantUMLEncoder.toHex(input));
    }

    @Test
    public void testToBase64() {
        String input = "test";
        String expected = "dGVzdA==";
        assertEquals(expected, PlantUMLEncoder.toBase64(input));
    }

    @Test
    public void testToURL() {
        String input = "test";
        String expected = "test";
        assertEquals(expected, PlantUMLEncoder.toURL(input));
    }

    @Test
    public void testCompress() {
        String input = "test";
        String compressed = PlantUMLEncoder.compress(input);
        assertNotNull(compressed);
    }

    @Test
    public void testDecompress() {
        String input = "test";
        String compressed = PlantUMLEncoder.compress(input);
        String decompressed = PlantUMLEncoder.decompress(compressed);
        assertEquals(input, decompressed);
    }
}
