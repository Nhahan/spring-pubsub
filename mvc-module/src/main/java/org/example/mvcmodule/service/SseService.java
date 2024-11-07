package org.example.mvcmodule.service;

import lombok.RequiredArgsConstructor;
import org.example.mvcmodule.event.SseEmitterHandler;
import org.example.mvcmodule.event.SseListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterHandler sseEmitterHandler;
    private final SseListener sseListener;

    public SseEmitter subscribe(String topic) {
        SseEmitter sseEmitter = sseEmitterHandler.addEmitter(topic);
        sseListener.subscribeToTopic(topic);
        return sseEmitter;
    }
}
