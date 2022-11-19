package com.k4ktyc.dockertelegramnotifier.docker.repository;

import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DockerEventRepository extends JpaRepository<DockerEventEntity, Long> {
}
