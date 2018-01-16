package org.alexburchak.place.backend.pixel;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.place.backend.batch.BatchProducer;
import org.alexburchak.place.common.data.Pixel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

/**
 * @author alexburchak
 */
@Service
@Slf4j
public class PixelConsumer {
    @Autowired
    private BatchProducer batchProducer;

    @KafkaListener(topics = "${backend.consumer-topic}")
    public void onPixel(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                        @Header(KafkaHeaders.OFFSET) Integer offset,
                        Pixel pixel) {

        log.info("Processing pixel {} on topic {} partition {} offset {}", pixel, topic, partition, offset);

        batchProducer.add(pixel);
    }
}
