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
    public void testPlantUMLEncode() {
        String input = "@startuml\r\n" + //
                "Alice -> Bob: Authentication Request\r\n" + //
                "Bob --> Alice: Authentication Response\r\n" + //
                "@enduml";
        String expected = "Syp9J4vLqBLJSCfFib9mB2t9ICqhoKnEBCdCprC8IYqiJIqkuGBAAUW2rO0LOr5LN92VLvpA1G00";
        assertEquals(expected, PlantUMLEncoder.plantUMLEncode(input));
    }

}
