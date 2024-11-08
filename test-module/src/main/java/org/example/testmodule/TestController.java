package org.example.testmodule;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final SseClientService sseClientService;

    @GetMapping("/topics/{topic}/test")
    public String test(@PathVariable String topic, @RequestParam int messageCount) throws InterruptedException {
        sseClientService.start(topic, messageCount);
        return sseClientService.getDurations();
    }
}
