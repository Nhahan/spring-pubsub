package org.example.testmodule;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseClientService {

    private final TestUrlConfig testUrlConfig;
    private final PublishService publishService;
    private final List<SseStrategy> strategies = new ArrayList<>();

    public synchronized void start(String topic, int messageCount) throws InterruptedException {
        int actualPublishCount = (int) (messageCount * 1.05);

        CountDownLatch connectedLatch = new CountDownLatch(2);
        strategies.addAll(SseStrategyFactory.createStrategies(testUrlConfig, messageCount, connectedLatch));
        strategies.parallelStream().forEach(SseStrategy::start);

        connectedLatch.await(3, TimeUnit.SECONDS);
        publishService.publishMessages(topic, actualPublishCount);
    }

    public String getDurations() {
        while (strategies.stream().anyMatch(s -> s.getDurationSeconds() == 0.0)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread was interrupted while waiting for duration", e);
                break;
            }
        }

        StringBuilder result = new StringBuilder();
        for (SseStrategy strategy : strategies) {
            result.append(String.format("%s SSE Duration: %.3f seconds%n", strategy.getSourceName(), strategy.getDurationSeconds()));
            strategy.close();
        }
        strategies.clear();

        return result.toString();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down SseClientService and strategies...");
        for (SseStrategy strategy : strategies) {
            strategy.close();
        }
        strategies.clear();

        publishService.shutdownExecutorService();
    }
}
