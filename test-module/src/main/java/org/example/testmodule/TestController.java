package org.example.testmodule;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final SseClientService sseClientService;
    private final StringRedisTemplate redisTemplate;

    @GetMapping("/topics/{topic}/test")
    public String test(@PathVariable String topic, @RequestParam int messageCount) throws InterruptedException {
        sseClientService.start(topic, messageCount);
        return sseClientService.getDurations();
    }

    @PostMapping("/topics/{topic}/publish")
    public String publish(@PathVariable String topic) {
        String uuid = UUID.randomUUID().toString();
        redisTemplate.convertAndSend(topic, uuid);
        return uuid;
    }
}
