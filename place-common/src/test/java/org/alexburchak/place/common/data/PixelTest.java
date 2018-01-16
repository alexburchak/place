package org.alexburchak.place.common.data;

import org.testng.annotations.Test;
import org.apache.commons.codec.binary.Hex;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author alexburchak
 */
public class PixelTest {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;
    private static final byte COLOR = 4;
    private static final int PARTITION_SIZE = 30;

    @Test
    public void testIsValid() {
        assertFalse(new Pixel(-1, 0, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertTrue(new Pixel(0, 0, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertTrue(new Pixel(WIDTH - 1, 0, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertFalse(new Pixel(WIDTH, 0, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));

        assertFalse(new Pixel(0, -1, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertTrue(new Pixel(0, 0, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertTrue(new Pixel(0, HEIGHT - 1, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertFalse(new Pixel(0, HEIGHT, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));

        assertFalse(new Pixel(-1, -1, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
        assertFalse(new Pixel(WIDTH, HEIGHT, (byte) 0).isValid(WIDTH, HEIGHT, COLOR));
    }

    @Test
    public void testGetPartition() {
        assertEquals(new Pixel(0, 0, (byte) 0).getPartition(WIDTH, PARTITION_SIZE), 0);

        for (int partition = 0; ; partition++) {
            int offset = PARTITION_SIZE * (partition + 1) - 1;

            assertEquals(new Pixel(offset % WIDTH, offset / WIDTH, (byte) 0).getPartition(WIDTH, PARTITION_SIZE), partition);

            if (offset > WIDTH * HEIGHT) {
                break;
            }
        }
    }

    @Test
    public void testToBytes2() {
        assertEquals(Hex.encodeHexString(new Pixel(0x00, 0x00, (byte) 0x00).toBytes2()), "0000");
        assertEquals(Hex.encodeHexString(new Pixel(0x0A, 0x0A, (byte) 0x0A).toBytes2()), "28aa");
        assertEquals(Hex.encodeHexString(new Pixel(0x0F, 0x0F, (byte) 0x0F).toBytes2()), "3cff");
        assertEquals(Hex.encodeHexString(new Pixel(0xF0, 0xF0, (byte) 0xF0).toBytes2()), "c300");
    }

    @Test
    public void testToBytes3() {
        assertEquals(Hex.encodeHexString(new Pixel(0x00, 0x00, (byte) 0x00).toBytes3()), "000000");
        assertEquals(Hex.encodeHexString(new Pixel(0x0A, 0x0A, (byte) 0x0A).toBytes3()), "0280aa");
        assertEquals(Hex.encodeHexString(new Pixel(0x0F, 0x0F, (byte) 0x0F).toBytes3()), "03c0ff");
        assertEquals(Hex.encodeHexString(new Pixel(0xF0, 0xF0, (byte) 0xF0).toBytes3()), "3c0f00");
    }

    @Test
    public void testParseBytes2() {
        assertEquals(Pixel.parseBytes16(new byte[]{0x00, 0x00}, 0), new Pixel(0x00, 0x00, (byte) 0x00));
        assertEquals(Pixel.parseBytes16(new byte[]{0x28, (byte) 0xAA}, 0), new Pixel(0x0A, 0x0A, (byte) 0x0A));
        assertEquals(Pixel.parseBytes16(new byte[]{0x3C, (byte) 0xFF}, 0), new Pixel(0x0F, 0x0F, (byte) 0x0F));
        assertEquals(Pixel.parseBytes16(new byte[]{(byte) 0xC3, 0x00}, 0), new Pixel(0x30, 0x30, (byte) 0x00));
    }

    @Test
    public void testParseBytes3() {
        assertEquals(Pixel.parseBytes24(new byte[]{0x00, 0x00, 0x00}, 0), new Pixel(0x00, 0x00, (byte) 0x00));
        assertEquals(Pixel.parseBytes24(new byte[]{0x02, (byte) 0x80, (byte) 0xAA}, 0), new Pixel(0x0A, 0x0A, (byte) 0x0A));
        assertEquals(Pixel.parseBytes24(new byte[]{0x03, (byte) 0xC0, (byte) 0xFF}, 0), new Pixel(0x0F, 0x0F, (byte) 0x0F));
        assertEquals(Pixel.parseBytes24(new byte[]{0x3C, 0x0F, 0x00}, 0), new Pixel(0xF0, 0xF0, (byte) 0x00));
    }
}
