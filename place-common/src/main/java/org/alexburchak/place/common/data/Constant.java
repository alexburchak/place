package org.alexburchak.place.common.data;

import java.math.BigDecimal;

/**
 * @author alexburchak
 */
public interface Constant {
    int MAX_WIDTH = 100;

    int MAX_HEIGHT = 100;

    /**
     * @see #COLOR_SIZE_IN_BITS
     */
    byte MAX_COLOR = 16;

    /**
     * @see #MAX_COLOR
     */
    int COLOR_SIZE_IN_BITS = (int) Math.ceil(Math.log(MAX_COLOR) / Math.log(2));

    /**
     * Defines how much information we keep to represent each pixel.<p>
     * If {@link #MAX_WIDTH} is {@code 1000} and {@link #MAX_HEIGHT} is {@code 1000} and {@link #MAX_COLOR} is {@code 16}, we can:
     * <ol>
     *     <li>for {@code X} axis, represent any value in {@code 10} bits</li>
     *     <li>for {@code Y} axis, represent any value in {@code 10} bits</li>
     *     <li>for color, represent any value in {@code 4} bits</li>
     * </ol>
     * This gives us exactly {@code 24} bits, or {@code 3} bytes
     *
     * @see Pixel#toBytes2
     * @see Pixel#toBytes3
     */
    int PIXEL_SIZE_IN_BYTES = (
            (int) Math.ceil(Math.log(MAX_WIDTH) / Math.log(2))
                    + (int) Math.ceil(Math.log(MAX_HEIGHT) / Math.log(2))
                    + COLOR_SIZE_IN_BITS
                    + Byte.SIZE
                    - 1
    ) / Byte.SIZE;

    /**
     * Desired number of partitions
     */
    int PARTITION_COUNT = 10;

    /**
     * Partition size, rounded up to cover all possible values
     */
    int PARTITION_SIZE = new BigDecimal(Constant.MAX_WIDTH * Constant.MAX_HEIGHT)
            .divide(new BigDecimal(PARTITION_COUNT), 0, BigDecimal.ROUND_UP)
            .intValue();
}
