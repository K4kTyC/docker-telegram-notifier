package com.k4ktyc.dockertelegramnotifier.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.k4ktyc.dockertelegramnotifier.docker.callback.DockerEventListenerCallback;
import com.k4ktyc.dockertelegramnotifier.docker.callback.DockerPastEventListenerCallback;
import com.k4ktyc.dockertelegramnotifier.docker.events.DockerEventListenerStoppedEvent;
import com.k4ktyc.dockertelegramnotifier.docker.mapper.EventMapper;
import com.k4ktyc.dockertelegramnotifier.telegram.service.TelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class DockerEventListenerService {

    private final DockerClient dockerClient;
    private final EventMapper eventMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final DockerEventService eventService;

    private final TelegramBot telegramBot;

    private int restartAttempts = 0;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


    @EventListener(ApplicationReadyEvent.class)
    public void startListener() {
        Instant now = Instant.now();
        dockerClient.eventsCmd()
                .withUntil(now.getEpochSecond() + "." + now.getNano())
                .exec(new DockerPastEventListenerCallback(eventMapper, eventPublisher, eventService));

        dockerClient.eventsCmd()
                .withSince(now.getEpochSecond() + "." + now.getNano())
                .exec(new DockerEventListenerCallback(eventMapper, eventPublisher));
    }

    @EventListener(DockerEventListenerStoppedEvent.class)
    public void restartListener() {
        if (restartAttempts >= 50) {
            telegramBot.sendMessageToSubscribedUsers("Unable to restart docker event listener");
        } else {
            if (restartAttempts++ % 5 != 0) {
                startListener();
            } else {
                scheduledExecutorService.schedule(this::startListener, 5, TimeUnit.MINUTES);
            }
        }
    }
}
