package org.example.testmodule;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class CustomEventHandler implements EventHandler {

    private final String sourceName;
    private final Consumer<String> messageCallback;
    private final CountDownLatch connectedLatch;

    @Override
    public void onOpen() {
        log.info("[{}] Connection opened.", sourceName);
        if (connectedLatch != null) {
            connectedLatch.countDown();
        }
    }

    @Override
    public void onClosed() {
        log.info("[{}] Connection closed.", sourceName);
    }

    @Override
    public void onMessage(String event, MessageEvent messageEvent) {
        messageCallback.accept(sourceName);
    }

    @Override
    public void onComment(String comment) {
    }

    @Override
    public void onError(Throwable t) {
        log.error("[{}] SSE error occurred: ", sourceName, t);
    }
}
