package org.example.testmodule;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @GetMapping("/test/{messageCount}")
    public String comparePerformance(@PathVariable int messageCount) {
        return testService.comparePerformance(messageCount);
    }
}
