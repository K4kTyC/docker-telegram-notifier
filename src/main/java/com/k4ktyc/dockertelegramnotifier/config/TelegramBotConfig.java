package com.k4ktyc.dockertelegramnotifier.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
public class TelegramBotConfig {

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("#{'${telegram.bot.allowedUserNames}'.split(',')}")
    private List<String> allowedUserNames;
}
