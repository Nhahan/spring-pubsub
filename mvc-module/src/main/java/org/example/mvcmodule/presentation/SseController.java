package org.example.mvcmodule.presentation;

import lombok.RequiredArgsConstructor;
import org.example.mvcmodule.service.SseService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/topics/{topic}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String topic) {
        return sseService.subscribe(topic);
    }
}
