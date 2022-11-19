package com.k4ktyc.dockertelegramnotifier.docker.service;

import com.k4ktyc.dockertelegramnotifier.docker.dto.PastDockerEvents;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import com.k4ktyc.dockertelegramnotifier.docker.repository.DockerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DockerEventService {

    private final DockerEventRepository eventRepository;


    @EventListener
    public void processPastDockerEvent(PastDockerEvents pastDockerEvents) {
        List<DockerEventEntity> existingEvents = eventRepository.findAll();
        List<DockerEventEntity> pastEvents = pastDockerEvents.getEvents();
        pastEvents.removeAll(existingEvents);
        eventRepository.saveAll(pastEvents);
        log.info("New past events found: " + pastEvents.size());
    }

    @EventListener
    public void processDockerEvent(DockerEventEntity eventEntity) {

    }
}
