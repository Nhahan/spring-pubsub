package org.example.testmodule;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebFluxSseStrategy implements SseStrategy {

    private final String sourceName = "WebFlux";
    private final String url;
    private final int messageCount;
    private final CountDownLatch connectedLatch;
    private EventSource eventSource;
    private final AtomicInteger counter = new AtomicInteger(0);
    private long startTime;
    private double durationSeconds;

    public WebFluxSseStrategy(String url, int messageCount, CountDownLatch connectedLatch) {
        this.url = url;
        this.messageCount = messageCount;
        this.connectedLatch = connectedLatch;
    }

    @Override
    public void start() {
        try {
            EventHandler handler = new CustomEventHandler(sourceName, this::handleMessage, connectedLatch);
            URI uri = URI.create(url);
            eventSource = new EventSource.Builder(handler, uri).build();
            eventSource.start();
            log.info("[{}] EventSource started and attempting to connect.", sourceName);
        } catch (Exception e) {
            log.error("[{}] Failed to start EventSource: {}", sourceName, e.getMessage(), e);
        }
    }

    @Override
    public void handleMessage(String sourceName) {
        int currentCount = counter.incrementAndGet();
        if (currentCount == 1) {
            startTime = System.currentTimeMillis();
        }
        if (currentCount == messageCount) {
            long endTime = System.currentTimeMillis();
            durationSeconds = (endTime - startTime) / 1000.0;
            log.info("[{}] Received {} messages in {} seconds", sourceName, messageCount, durationSeconds);
        }
    }

    @Override
    public double getDurationSeconds() {
        return durationSeconds;
    }

    @Override
    public void close() {
        if (eventSource != null) {
            try {
                eventSource.close();
                log.info("[{}] EventSource closed.", sourceName);
            } catch (Exception e) {
                log.error("[{}] Error closing EventSource: {}", sourceName, e.getMessage(), e);
            }
        }
    }

    @Override
    public String getSourceName() {
        return sourceName;
    }
}
