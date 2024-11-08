package org.example.testmodule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SseStrategyFactory {

    public static List<SseStrategy> createStrategies(TestUrlConfig config, int messageCount, CountDownLatch latch) {
        List<SseStrategy> strategies = new ArrayList<>();

        if (config.getMvcUrl() != null && !config.getMvcUrl().isEmpty()) {
            strategies.add(new MvcSseStrategy(config.getMvcUrl(), messageCount, latch));
        }

        if (config.getWebfluxUrl() != null && !config.getWebfluxUrl().isEmpty()) {
            strategies.add(new WebFluxSseStrategy(config.getWebfluxUrl(), messageCount, latch));
        }

        return strategies;
    }
}
