package com.k4ktyc.dockertelegramnotifier.docker;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class DockerController {

    private final DockerEventsService dockerEventsService;

    @GetMapping("/start")
    public void start() {
        dockerEventsService.startListening();
    }

    @GetMapping("/stop")
    public void stop() throws IOException {
        dockerEventsService.stopListening();
    }
}
