package org.example.testmodule;

public interface SseStrategy {
    void start();

    void handleMessage(String sourceName);

    double getDurationSeconds();
    void close();

    String getSourceName();
}
