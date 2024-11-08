package org.example.testmodule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublishService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final RedisTemplate<String, String> redisTemplate;

    public void publishMessages(String topic, int messageCount) {
        List<Callable<Void>> tasks = createPublishTasks(topic, messageCount);

        try {
            List<Future<Void>> futures = executorService.invokeAll(tasks);

            for (Future<Void> future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    log.error("Error publishing messages: ", e.getCause());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Publishing messages was interrupted: ", e);
        }
    }

    @NotNull
    private List<Callable<Void>> createPublishTasks(String topic, int messageCount) {
        final int CHUNK_SIZE = 1000;
        List<Callable<Void>> tasks = new ArrayList<>();

        for (int start = 0; start < messageCount; start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, messageCount);
            int finalStart = start;
            tasks.add(() -> {
                for (int i = finalStart; i < end; i++) {
                    redisTemplate.convertAndSend(topic, "Test message " + i + "," + System.currentTimeMillis());
                }
                return null;
            });
        }
        log.info("Publishing messages in progress...");
        return tasks;
    }

    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    log.error("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
