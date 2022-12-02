package com.k4ktyc.dockertelegramnotifier.docker.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.k4ktyc.dockertelegramnotifier.docker.dto.NotPersistedPastDockerEvents;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import com.k4ktyc.dockertelegramnotifier.docker.repository.DockerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static com.github.dockerjava.api.model.EventType.CONTAINER;

@Slf4j
@RequiredArgsConstructor
@Service
public class DockerEventService {

    private final DockerEventRepository eventRepository;

    private final DockerClient dockerClient;


    public List<DockerEventEntity> findAll() {
        return eventRepository.findAll();
    }

    @EventListener
    public void persistPastDockerEvents(NotPersistedPastDockerEvents notPersistedPastDockerEvents) {
        eventRepository.saveAll(notPersistedPastDockerEvents.getEvents());
    }

    @EventListener
    public void persistDockerEvent(DockerEventEntity eventEntity) {
        eventRepository.save(eventEntity);
    }

    public String buildMessageForTelegram(DockerEventEntity eventEntity) {
        if (CONTAINER.equals(eventEntity.getEventType())) {
            Container container = getContainerById(eventEntity.getContainerId());

            String eventType = eventEntity.getEventType().getValue().toLowerCase();
            String containerName = container.getNames()[0];
            String image = container.getImage();
            String action = eventEntity.getAction();
            String timestamp = Instant.ofEpochSecond(0, eventEntity.getTimestampNano())
                    .atZone(ZoneId.of("Europe/Minsk"))
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));

            return StringUtils.capitalize(String.format("%s %s(%s): %s | %s",
                    eventType, containerName, image, action, timestamp));
        } else {
            return "";
        }
    }

    private Container getContainerById(String id) {
        return dockerClient.listContainersCmd()
                .withIdFilter(Collections.singletonList(id))
                .exec()
                .get(0);
    }
}
