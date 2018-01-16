package org.alexburchak.place.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author alexburchak
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Pixel {
    @JsonProperty("x")
    private int x;
    @JsonProperty("y")
    private int y;
    @JsonProperty("c")
    private byte c;

    @Override
    public String toString() {
        return "\u2192" + x + "\u2193" + y + "\u2610" + c;
    }

    @JsonIgnore
    public boolean isValid(int width, int height, byte color) {
        return x >= 0 && x < width
                && y >= 0 && y < height
                && c >= 0 && c < color;
    }

    @JsonIgnore
    public int getPartition(int width, int partitionSize) {
        return (x + y * width) / partitionSize;
    }

    /**
     * 16-bit representation version
     *
     * @see Constant#PIXEL_SIZE_IN_BYTES
     */
    @JsonIgnore
    public byte[] toBytes2() {
        return new byte[]{
                (byte) (((x << 2) & 0x0FC) | ((y >> 6) & 0x03)),
                (byte) (((y << 4) & 0x0F0) | (c & 0x0F))
        };
    }

    /**
     * 24-bit representation version
     *
     * @see Constant#PIXEL_SIZE_IN_BYTES
     */
    @JsonIgnore
    public byte[] toBytes3() {
        return new byte[]{
                (byte) (x >>> 2),
                (byte) (((x << 6) & 0xC0) | ((y >> 4) & 0x3F)),
                (byte) (((y << 4) & 0x0F0) | (c & 0x0F))
        };
    }

    /**
     * 16-bit representation version
     *
     * @see Constant#PIXEL_SIZE_IN_BYTES
     */
    @JsonIgnore
    public static Pixel parseBytes16(byte bytes[], int offset) {
        return new Pixel(
                (bytes[offset] >>> 2) & 0x3F,
                ((bytes[offset] << 4) & 0x30) | ((bytes[offset + 1] >> 4) & 0x0F),
                (byte) (bytes[offset + 1] & 0x0F)
        );
    }

    /**
     * 24-bit representation version
     *
     * @see Constant#PIXEL_SIZE_IN_BYTES
     */
    @JsonIgnore
    public static Pixel parseBytes24(byte bytes[], int offset) {
        return new Pixel(
                ((bytes[offset] << 2) & 0x03FF) | ((bytes[offset + 1] >> 6) & 0x0003),
                ((bytes[offset + 1] << 4) & 0x03FF) | ((bytes[offset + 2] >> 4) & 0x0F),
                (byte) (bytes[offset + 2] & 0x0F)
        );
    }
}
