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
        removeSessionTopicFromRedis(session);
    }

    public void registerSessionTopic(WebSocketSession session, String topic) {
        redisTemplate.opsForValue().set(getSessionTopicKey(session), topic);
    }

    public void sendMessageToTopic(WebSocketSession session, String payload) {
        String topic = getSessionTopic(session);
        if (topic == null) {
            log.warn("No topic found for session {}", session.getId());
            return;
        }
        redisTemplate.convertAndSend(topic, payload);
    }

    public void broadcast(String message, String topic) {
        TextMessage textMessage = new TextMessage(message);
        sessions.forEach(session -> {
            if (isSessionSubscribedToTopic(session, topic)) {
                sendMessageToSession(session, textMessage);
            }
        });
    }

    private void sendMessageToSession(WebSocketSession session, TextMessage message) {
        try {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        } catch (Exception e) {
            log.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
        }
    }

    private String getSessionTopic(WebSocketSession session) {
        return redisTemplate.opsForValue().get(getSessionTopicKey(session));
    }

    private boolean isSessionSubscribedToTopic(WebSocketSession session, String topic) {
        String sessionTopic = getSessionTopic(session);
        return sessionTopic != null && sessionTopic.equals(topic);
    }

    private void removeSessionTopicFromRedis(WebSocketSession session) {
        redisTemplate.delete(getSessionTopicKey(session));
    }

    private String getSessionTopicKey(WebSocketSession session) {
        return "session:" + session.getId() + ":topic";
    }
}
