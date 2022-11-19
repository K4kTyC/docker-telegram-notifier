package com.k4ktyc.dockertelegramnotifier.docker.service;

import com.k4ktyc.dockertelegramnotifier.docker.dto.NotPersistedPastDockerEvents;
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
}
