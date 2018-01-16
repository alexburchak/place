package org.alexburchak.place.frontend.ws;

import org.alexburchak.place.common.data.Constant;
import org.alexburchak.place.common.data.Pixel;
import org.apache.commons.codec.binary.Hex;

/**
 * @author alexburchak
 */
public class Snapshot {
    private static final byte SNAPSHOT[] = new byte[Constant.MAX_WIDTH * Constant.MAX_HEIGHT * Constant.COLOR_SIZE_IN_BITS / Byte.SIZE];

    public void update(byte bytes[]) {
        for (int i = 0; i < bytes.length; i += Constant.PIXEL_SIZE_IN_BYTES) {
            Pixel pixel = Constant.PIXEL_SIZE_IN_BYTES == 2
                    ? Pixel.parseBytes16(bytes, i)
                    : Pixel.parseBytes24(bytes, i);

            int index = pixel.getX() + pixel.getY() * Constant.MAX_WIDTH;
            int base = index * Constant.COLOR_SIZE_IN_BITS / Byte.SIZE;
            int shift = index * Constant.COLOR_SIZE_IN_BITS % Byte.SIZE;

            SNAPSHOT[base] = (byte) ((SNAPSHOT[base] & (0xF << shift)) | (pixel.getC() << (Constant.COLOR_SIZE_IN_BITS - shift)));
        }
    }

    @Override
    public String toString() {
        return Hex.encodeHexString(SNAPSHOT);
    }
}
