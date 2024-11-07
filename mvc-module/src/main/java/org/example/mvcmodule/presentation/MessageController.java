package org.example.mvcmodule.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final StringRedisTemplate redisTemplate;

    @PostMapping("/topics/{topic}/publish")
    public ResponseEntity<Void> publishMessage(@PathVariable String topic) {
        redisTemplate.convertAndSend(topic, UUID.randomUUID().toString());
        return ResponseEntity.ok().build();
    }
}
