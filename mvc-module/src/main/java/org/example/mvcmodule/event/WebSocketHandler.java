package org.example.mvcmodule.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler {

    private final StringRedisTemplate redisTemplate;

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    public void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public void removeSession(WebSocketSession session) {
        sessions.remove(session);
        redisTemplate.delete("session:" + session.getId() + ":topic");
    }

    public void registerSessionTopic(WebSocketSession session, String topic) {
        redisTemplate.opsForValue().set("session:" + session.getId() + ":topic", topic);
    }

    public void sendMessageToTopic(WebSocketSession session, String payload) {
        String topic = redisTemplate.opsForValue().get("session:" + session.getId() + ":topic");
        if (topic == null) {
            throw new IllegalStateException("No topic found for session " + session.getId());
        }

        redisTemplate.convertAndSend(topic, payload);
    }

    public void broadcast(String message, String topic) {
        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession session : sessions) {
            String sessionTopic = redisTemplate.opsForValue().get("session:" + session.getId() + ":topic");
            if (sessionTopic != null && sessionTopic.equals(topic)) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                } catch (Exception e) {
                    log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
                }
            }
        }
    }
}
