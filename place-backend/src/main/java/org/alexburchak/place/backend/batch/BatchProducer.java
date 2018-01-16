package org.alexburchak.place.backend.batch;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.place.backend.config.BackendProperties;
import org.alexburchak.place.common.data.Constant;
import org.alexburchak.place.common.data.Pixel;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.util.function.Predicate;

/**
 * @author alexburchak
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class BatchProducer {
    @Autowired
    private BackendProperties backendProperties;
    @Autowired
    private KafkaTemplate<String, ByteBuffer> kafkaTemplate;

    private ByteBuffer buffer;

    @PostConstruct
    public void postConstruct() {
        buffer = ByteBuffer.allocate(backendProperties.getProducerBufferSize() * Constant.PIXEL_SIZE_IN_BYTES);
    }

    public  void add(Pixel pixel) {
        flush(b -> b.capacity() - b.position() < Constant.PIXEL_SIZE_IN_BYTES);

        buffer.put(Constant.PIXEL_SIZE_IN_BYTES == 2
                ? pixel.toBytes2()
                : pixel.toBytes3()
        );
    }

    @Scheduled(fixedRateString = "${backend.batch-flush-period}")
    public void scheduledFlush() {
        flush(b -> b.position() > 0);
    }

    private synchronized void flush(Predicate<ByteBuffer> predicate) {
        if (predicate.test(buffer)) {
            int size = buffer.position();

            buffer.rewind();
            buffer.limit(size);

            ByteBuffer copy = ByteBuffer.allocate(size);
            copy.put(buffer);
            copy.flip();

            buffer.clear();

            log.debug("Flushing batch buffer of {} bytes", copy.capacity());

            kafkaTemplate.send(kafkaTemplate.getDefaultTopic(), copy)
                    .addCallback(r -> {
                                RecordMetadata metadata = r.getRecordMetadata();
                                log.debug("Successfully sent batch of size {} bytes to partition {} offset {} at timestamp {}", size, metadata.partition(), metadata.offset(), metadata.timestamp());
                            }, e -> {
                                log.error("Failed sending batch of size {} bytes", size, e);
                            }
                    );
        }
    }
}
