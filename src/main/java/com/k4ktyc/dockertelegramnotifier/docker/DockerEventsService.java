package com.k4ktyc.dockertelegramnotifier.docker;

import com.github.dockerjava.api.DockerClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class DockerEventsService {

    private final DockerClient dockerClient;

    private DockerEventsProcessor eventsProcessor;

    @EventListener(ApplicationReadyEvent.class)
    public void startListening() {
        eventsProcessor = new DockerEventsProcessor();
        dockerClient.eventsCmd().withSince("0").exec(eventsProcessor);
    }

    public void stopListening() throws IOException {
        if (eventsProcessor != null) {
            eventsProcessor.close();
        }
    }
}
