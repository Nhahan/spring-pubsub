package org.example.mvcmodule.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SseListener implements MessageListener {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final SseEmitterHandler sseEmitterHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String topic = new String(message.getChannel());
        String data = new String(message.getBody());
        sseEmitterHandler.sendMessageToTopic(topic, data);
    }

    public void subscribeToTopic(String topic) {
        redisMessageListenerContainer.addMessageListener(this, new ChannelTopic(topic));
    }
}
