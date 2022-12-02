package com.k4ktyc.dockertelegramnotifier.telegram.repository;

import com.k4ktyc.dockertelegramnotifier.telegram.model.TelegramUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramUserRepository extends JpaRepository<TelegramUserEntity, Long> {

    @Query("select user.chatId from #{#entityName} user")
    List<Long> findAllChatIds();
}
