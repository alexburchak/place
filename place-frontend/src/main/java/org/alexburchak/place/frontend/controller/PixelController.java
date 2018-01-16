package org.alexburchak.place.frontend.controller;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.place.common.data.Constant;
import org.alexburchak.place.common.data.Pixel;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author alexburchak
 */
@RestController
@Slf4j
public class PixelController {
    public static final String PATH_PIXEL = "/pixel";

    public static final String PARAM_X = "x";
    public static final String PARAM_Y = "y";
    public static final String PARAM_C = "c";

    @Autowired
    private KafkaTemplate<String, Pixel> kafkaTemplate;

    @RequestMapping(path = PATH_PIXEL, method = RequestMethod.PUT)
    public Future<Long> pixel(@RequestParam(PARAM_X) int x, @RequestParam(PARAM_Y) int y, @RequestParam(PARAM_C) byte c, HttpServletResponse response) throws IOException {
        log.debug("Received pixel for x={}, y={}, c={}", x, y, c);

        Pixel pixel = new Pixel(x, y, c);
        if (!pixel.isValid(Constant.MAX_WIDTH, Constant.MAX_HEIGHT, Constant.MAX_COLOR)) {
            log.error("Invalid pixel: x={}, y={}, c={}", x, y, c);

            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return CompletableFuture.completedFuture(-1L);
        }

        // ensure the page is not cached so that the page will have reloaded for the new model attributes values
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, max-age=0");
        response.setDateHeader(HttpHeaders.EXPIRES, 0);

        int partition = pixel.getPartition(Constant.MAX_WIDTH, Constant.PARTITION_SIZE);
        log.debug("For pixel {} calculated partition {}", pixel, partition);

        ListenableFuture<SendResult<String, Pixel>> future = kafkaTemplate.sendDefault(partition, null, pixel);

        return CompletableFuture.supplyAsync(() -> {
            try {
                SendResult<String, Pixel> result = future.get();
                RecordMetadata metadata = result.getRecordMetadata();

                log.debug("Successfully sent pixel {} to partition {} offset {} at timestamp {}", pixel, metadata.partition(), metadata.offset(), metadata.timestamp());

                return metadata.timestamp();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Failed to send pixel {} to default topic", pixel, e);

                response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());

                return -1L;
            }
        });
    }
}
