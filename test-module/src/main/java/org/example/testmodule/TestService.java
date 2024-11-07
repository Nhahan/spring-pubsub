package org.example.testmodule;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class TestService {

    private static final String TOPIC_NAME = "test";
    private static final String MVC_URL = "http://localhost:8081/topics/test/subscribe";
    private static final String WEBFLUX_URL = "http://localhost:8082/topics/test/subscribe";

    private final StringRedisTemplate redisTemplate;

    // 성능 비교를 위한 API
    public String comparePerformance(int messageCount) {
        AtomicInteger mvcReceivedCount = new AtomicInteger(0);
        AtomicInteger webfluxReceivedCount = new AtomicInteger(0);
        AtomicLong mvcTotalLatency = new AtomicLong(0);
        AtomicLong webfluxTotalLatency = new AtomicLong(0);

        // mvc-module에 SSE 연결
        WebClient mvcClient = WebClient.create();
        Flux<Long> mvcFlux = mvcClient.get()
                .uri(MVC_URL)
                .retrieve()
                .bodyToFlux(String.class)
                .map(message -> {
                    long latency = calculateLatency();
                    mvcTotalLatency.addAndGet(latency);
                    mvcReceivedCount.incrementAndGet();
                    return latency;
                });

        // webflux-module에 SSE 연결
        WebClient webfluxClient = WebClient.create();
        Flux<Long> webfluxFlux = webfluxClient.get()
                .uri(WEBFLUX_URL)
                .retrieve()
                .bodyToFlux(String.class)
                .map(message -> {
                    long latency = calculateLatency();
                    webfluxTotalLatency.addAndGet(latency);
                    webfluxReceivedCount.incrementAndGet();
                    return latency;
                });

        // Redis에 메시지를 발행하여 테스트 시작
        publishMessages(messageCount);

        // 두 Flux의 모든 데이터 수신을 기다림
        Mono.zip(mvcFlux.take(messageCount).then(), webfluxFlux.take(messageCount).then())
                .block();

        // mvc-module과 webflux-module의 평균 응답 시간 계산
        long mvcAvgLatency = mvcReceivedCount.get() > 0 ? mvcTotalLatency.get() / mvcReceivedCount.get() : 0;
        long webfluxAvgLatency = webfluxReceivedCount.get() > 0 ? webfluxTotalLatency.get() / webfluxReceivedCount.get() : 0;

        return String.format("""
                        Performance Comparison:
                        - mvc-module average response time: %d ms
                        - webflux-module average response time: %d ms
                        Total messages: %d""",
                mvcAvgLatency, webfluxAvgLatency, messageCount);
    }

    // 메시지 발행 메서드
    private void publishMessages(int messageCount) {
        for (int i = 0; i < messageCount; i++) {
            redisTemplate.convertAndSend(TOPIC_NAME, "Test message " + i);
        }
    }

    // 현재 시간으로부터 지연 시간 계산
    private long calculateLatency() {
        Instant now = Instant.now();
        return Duration.between(now.minusMillis(50), now).toMillis();  // 메시지 간격 조절
    }
}
