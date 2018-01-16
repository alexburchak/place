package org.alexburchak.place.frontend.ws;

import lombok.extern.slf4j.Slf4j;
import org.alexburchak.place.common.data.Constant;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @author alexburchak
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Slf4j
public class PixelWebSocketHandler extends TextWebSocketHandler {
    private static final Map<String, WebSocketSession> SESSIONS = Collections.synchronizedMap(new TreeMap<>());
    private static final Snapshot SNAPSHOT = new Snapshot();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        log.debug("Established WebSocket connection for session {}", session.getId());

        TextMessage message = new TextMessage("S" + SNAPSHOT.toString());
        session.sendMessage(message);

        SESSIONS.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        log.debug("Closing WebSocket session {} on connection close", session.getId());

        SESSIONS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();

        log.debug("Received message {}", payload);
    }

    @KafkaListener(
            topics = "${frontend.consumer-topic}",
            topicPartitions = @TopicPartition(topic = "${frontend.consumer-topic}", partitionOffsets = @PartitionOffset(partition = "0", initialOffset = "0"))
    )
    public void onBatch(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                        @Header(KafkaHeaders.OFFSET) Integer offset,
                        ByteBuffer batch) {

        log.info("Processing batch on topic {} partition {} offset {} size {}", topic, partition, offset, batch.capacity());

        // update snapshot
        byte bytes[] = batch.array();
        SNAPSHOT.update(bytes);

        // update clients
        Collection<WebSocketSession> safe;
        synchronized (SESSIONS) {
            if (SESSIONS.isEmpty()) {
                return;
            }

            safe = new ArrayList<>(SESSIONS.values());
        }

        TextMessage message = new TextMessage("B" + Constant.PIXEL_SIZE_IN_BYTES + Hex.encodeHexString(bytes));
        safe.forEach(wss -> {
            try {
                wss.sendMessage(message);
            } catch (IOException e) {
                log.error("Failed to send message of size {} to WebSocket session {}: {}", batch.capacity(), wss.getId(), e.getMessage());
            }
        });
    }
}
