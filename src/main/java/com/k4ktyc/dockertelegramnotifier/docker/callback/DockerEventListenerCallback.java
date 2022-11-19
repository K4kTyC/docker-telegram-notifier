package com.k4ktyc.dockertelegramnotifier.docker.callback;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.k4ktyc.dockertelegramnotifier.docker.mapper.EventMapper;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.Closeable;

@Slf4j
@RequiredArgsConstructor
@Component
public class DockerEventListenerCallback extends ResultCallback.Adapter<Event> {

    private final EventMapper eventMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public void onStart(Closeable stream) {
        super.onStart(stream);
        log.info("Start listening for events");
    }

    @Override
    public void onNext(Event object) {
        DockerEventEntity eventEntity = eventMapper.toDockerEventEntity(object);
        eventPublisher.publishEvent(eventEntity);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        log.info("Stop listening for events");
    }
}
