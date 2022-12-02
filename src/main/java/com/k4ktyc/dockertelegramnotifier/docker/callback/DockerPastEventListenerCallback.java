package com.k4ktyc.dockertelegramnotifier.docker.callback;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.k4ktyc.dockertelegramnotifier.docker.dto.NotPersistedPastDockerEvents;
import com.k4ktyc.dockertelegramnotifier.docker.mapper.EventMapper;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import com.k4ktyc.dockertelegramnotifier.docker.service.DockerEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DockerPastEventListenerCallback extends ResultCallback.Adapter<Event> {

    private final EventMapper eventMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final DockerEventService eventService;

    private List<DockerEventEntity> pastEvents;


    @Override
    public void onStart(Closeable stream) {
        super.onStart(stream);
        log.info("Start searching for past events");
        pastEvents = new ArrayList<>();
    }

    @Override
    public void onNext(Event object) {
        DockerEventEntity eventEntity = eventMapper.toDockerEventEntity(object);
        pastEvents.add(eventEntity);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        List<DockerEventEntity> persistedEvents = eventService.findAll();
        pastEvents.removeAll(persistedEvents);
        log.info("Stop searching for past events (found: " + pastEvents.size() + ")");
        eventPublisher.publishEvent(new NotPersistedPastDockerEvents(pastEvents));
    }
}
