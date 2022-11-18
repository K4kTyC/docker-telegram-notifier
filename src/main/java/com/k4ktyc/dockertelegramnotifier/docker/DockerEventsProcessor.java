package com.k4ktyc.dockertelegramnotifier.docker;

import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

@Slf4j
@RequiredArgsConstructor
public class DockerEventsProcessor extends ResultCallback.Adapter<Event> {

    @Override
    public void onStart(Closeable stream) {
        super.onStart(stream);
        log.info("started listening");
    }

    @Override
    public void onNext(Event object) {
        log.info("Event occurred: " + object.getType());
    }

    @Override
    public void onComplete() {
        super.onComplete();
        log.info("stopped listening");
    }
}
