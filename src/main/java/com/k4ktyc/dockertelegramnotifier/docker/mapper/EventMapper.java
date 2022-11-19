package com.k4ktyc.dockertelegramnotifier.docker.mapper;

import com.github.dockerjava.api.model.Event;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "type", target = "eventType")
    @Mapping(source = "action", target = "action")
    @Mapping(source = "from", target = "imageTag")
    @Mapping(source = "id", target = "containerId")
    @Mapping(source = "timeNano", target = "timestampNano")
    DockerEventEntity toDockerEventEntity(Event event);
}
