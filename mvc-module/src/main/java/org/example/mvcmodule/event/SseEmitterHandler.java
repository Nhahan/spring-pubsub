package org.example.mvcmodule.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SseEmitterHandler {

    private final StringRedisTemplate redisTemplate;
    private final Map<String, Set<SseEmitter>> emittersPerTopic = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(String topic) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emittersPerTopic.computeIfAbsent(topic, key -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(emitter);

        emitter.onCompletion(() -> removeEmitter(topic, emitter));
        emitter.onTimeout(() -> removeEmitter(topic, emitter));
        emitter.onError(e -> removeEmitter(topic, emitter));

        registerEmitterInRedis(topic, emitter);
        return emitter;
    }

    private void registerEmitterInRedis(String topic, SseEmitter emitter) {
        redisTemplate.opsForSet().add("subscribe:" + topic, String.valueOf(emitter.hashCode()));
    }

    private void removeEmitter(String topic, SseEmitter emitter) {
        Set<SseEmitter> emitters = emittersPerTopic.get(topic);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersPerTopic.remove(topic);
            }
        }
    }

    public void broadcast(String topic, String data) {
        Set<SseEmitter> emitters = emittersPerTopic.get(topic);
        if (emitters != null) {
            emitters.removeIf(emitter -> !sendEvent(emitter, data));
        }
    }

    private boolean sendEvent(SseEmitter emitter, String data) {
        try {
            emitter.send(SseEmitter.event().name("message").data(data));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
