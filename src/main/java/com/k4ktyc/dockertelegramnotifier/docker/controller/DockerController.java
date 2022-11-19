package com.k4ktyc.dockertelegramnotifier.docker.controller;

import com.k4ktyc.dockertelegramnotifier.docker.service.DockerEventListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class DockerController {

    private final DockerEventListenerService dockerEventListenerService;

    @GetMapping("/start")
    public void start() {
        dockerEventListenerService.startListening();
    }

    @GetMapping("/stop")
    public void stop() throws IOException {
        dockerEventListenerService.stopListening();
    }
}
