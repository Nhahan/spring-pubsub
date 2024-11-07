package org.example.mvcmodule.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketListener implements MessageListener {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final WebSocketHandler webSocketHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        String topic = new String(pattern, StandardCharsets.UTF_8);
        webSocketHandler.broadcast(msg, topic);
    }

    public void subscribeToTopic(String topic) {
        redisMessageListenerContainer.addMessageListener(this, new ChannelTopic(topic));
    }
}
