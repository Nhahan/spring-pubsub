package org.example.webfluxmodule.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

@RestController
@RequiredArgsConstructor
public class SseController {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @GetMapping(value = "/topics/{topic}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamMessages(@PathVariable String topic) {
        return reactiveRedisTemplate.listenTo(new ChannelTopic(topic))
                .map(message -> ServerSentEvent.<String>builder()
                        .data(message.getMessage())
                        .build());
    }

    @GetMapping(value = "/ping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> ping() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(tick -> ServerSentEvent.<String>builder()
                        .event("ping")
                        .data(String.valueOf(Instant.now().toEpochMilli()))
                        .build());
    }
}
