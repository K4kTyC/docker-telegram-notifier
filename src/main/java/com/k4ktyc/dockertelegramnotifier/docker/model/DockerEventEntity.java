package com.k4ktyc.dockertelegramnotifier.docker.model;

import com.github.dockerjava.api.model.EventType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "docker_event")
public class DockerEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String action;
    private String imageTag;
    private String containerId;
    private Long timestampNano;
}
