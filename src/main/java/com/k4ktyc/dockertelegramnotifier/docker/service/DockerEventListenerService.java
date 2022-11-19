package com.k4ktyc.dockertelegramnotifier.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.k4ktyc.dockertelegramnotifier.docker.callback.DockerEventListenerCallback;
import com.k4ktyc.dockertelegramnotifier.docker.callback.DockerPastEventListenerCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Component
public class DockerEventListenerService {

    private final DockerClient dockerClient;
    private final DockerPastEventListenerCallback pastEventListenerCallback;
    private final DockerEventListenerCallback eventListenerCallback;


    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        dockerClient.eventsCmd()
                .withUntil(String.valueOf(Instant.now().getEpochSecond()))
                .exec(pastEventListenerCallback);

        dockerClient.eventsCmd()
                .exec(eventListenerCallback);
    }

    public void stopListening() throws IOException {
        eventListenerCallback.close();
    }
}
