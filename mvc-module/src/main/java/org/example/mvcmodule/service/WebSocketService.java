package org.example.mvcmodule.service;

import lombok.RequiredArgsConstructor;
import org.example.mvcmodule.event.WebSocketHandler;
import org.example.mvcmodule.event.WebSocketListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final WebSocketHandler webSocketHandler;
    private final WebSocketListener webSocketListener;

    public void addSession(WebSocketSession session) {
        webSocketHandler.addSession(session);
    }

    public void removeSession(WebSocketSession session) {
        webSocketHandler.removeSession(session);
    }

    public void handlePayload(WebSocketSession session, String payload) {
        if (payload.startsWith("subscribe:")) {
            String topic = payload.substring("subscribe:".length()).trim();
            subscribeToTopic(session, topic);
            return;
        }

        webSocketHandler.sendMessageToTopic(session, payload);
    }

    private void subscribeToTopic(WebSocketSession session, String topic) {
        webSocketHandler.registerSessionTopic(session, topic);
        webSocketListener.subscribeToTopic(topic);
    }
}
