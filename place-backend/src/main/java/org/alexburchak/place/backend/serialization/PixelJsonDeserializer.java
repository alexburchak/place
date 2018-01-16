package org.alexburchak.place.backend.serialization;

import org.alexburchak.place.common.data.Pixel;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/**
 * @author alexburchak
 */
public class PixelJsonDeserializer extends JsonDeserializer<Pixel> {
    public PixelJsonDeserializer() {
        super(Pixel.class);
    }
}
