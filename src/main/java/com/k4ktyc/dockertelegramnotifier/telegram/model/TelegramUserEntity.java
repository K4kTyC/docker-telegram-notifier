package com.k4ktyc.dockertelegramnotifier.telegram.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Locale;

@Data
@Entity
@Table(name = "telegram_user")
public class TelegramUserEntity {

    @Id
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "subscribed", nullable = false)
    private Boolean subscribed;

    @Column(name = "locale", nullable = false)
    private Locale locale;

}
