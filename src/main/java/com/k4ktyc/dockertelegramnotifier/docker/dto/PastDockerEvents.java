package com.k4ktyc.dockertelegramnotifier.docker.dto;

import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class PastDockerEvents {

    private List<DockerEventEntity> events;
}
