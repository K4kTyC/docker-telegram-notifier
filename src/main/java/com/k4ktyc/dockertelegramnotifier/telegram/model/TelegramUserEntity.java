package com.k4ktyc.dockertelegramnotifier.telegram.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Table(name = "telegram_user")
@Entity
public class TelegramUserEntity {

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "user_name", nullable = false)
    private String userName;
}
