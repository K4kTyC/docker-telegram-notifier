package com.k4ktyc.dockertelegramnotifier.docker.callback;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.k4ktyc.dockertelegramnotifier.docker.dto.PastDockerEvents;
import com.k4ktyc.dockertelegramnotifier.docker.mapper.EventMapper;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class DockerPastEventListenerCallback extends ResultCallback.Adapter<Event> {

    private final EventMapper eventMapper;
    private final ApplicationEventPublisher eventPublisher;

    private List<DockerEventEntity> pastEvents;


    @Override
    public void onStart(Closeable stream) {
        super.onStart(stream);
        log.info("Started searching for past events");
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
        eventPublisher.publishEvent(new PastDockerEvents(pastEvents));
        log.info("Stopped searching for past events");
    }
}
