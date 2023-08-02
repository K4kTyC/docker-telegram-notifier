package com.k4ktyc.dockertelegramnotifier.telegram.service;

import com.k4ktyc.dockertelegramnotifier.config.TelegramBotConfig;
import com.k4ktyc.dockertelegramnotifier.docker.event.DockerEventListenerFailedToRestartEvent;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import com.k4ktyc.dockertelegramnotifier.docker.service.DockerEventService;
import com.k4ktyc.dockertelegramnotifier.telegram.model.TelegramUserEntity;
import com.k4ktyc.dockertelegramnotifier.telegram.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Locale;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotConfig botConfig;
    private final TelegramUserRepository userRepository;

    private final DockerEventService dockerEventService;

    private final MessageSource messageSource;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String userName = message.getFrom().getUserName();
            if (botConfig.getAllowedUserNames().contains(userName)) {
                Long chatId = message.getChatId();
                String text = message.getText();
                Locale userLocale = new Locale(message.getFrom().getLanguageCode());
                switch (text) {
                    case "/start" -> processStartCommand(chatId, userName, userLocale);
                    case "/suspend" -> processSuspendCommand(chatId, userLocale);
                    case "/resume" -> processResumeCommand(chatId, userLocale);
                }
            }
        }
    }

    @EventListener
    public void onDockerEventReceived(DockerEventEntity eventEntity) {
        String message = dockerEventService.buildMessageForTelegram(eventEntity);
        if (StringUtils.isNotBlank(message)) {
            sendMessageToSubscribedUsers(message);
        }
    }

    @EventListener(DockerEventListenerFailedToRestartEvent.class)
    public void onDockerEventListenerFailedToRestart() {
        sendMessageToSubscribedUsers("message.listener.restart_failed");
    }


    private void sendTelegramMessage(Long chatId, String messageToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendMessageToSubscribedUsers(String messageToSend) {
        sendMessageToSubscribedUsers(messageToSend, null);
    }

    public void sendMessageToSubscribedUsers(String messageToSend, Object[] args) {
        userRepository.findAllSubscribed().forEach(user -> {
            Long chatId = user.getChatId();
            Locale locale = user.getLocale();
            String message = getLocalizedMessage(messageToSend, args, locale);

            sendTelegramMessage(chatId, message);
        });
    }

    private String getLocalizedMessage(String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }


    private void processStartCommand(Long chatId, String userName, Locale locale) {
        if (!userRepository.existsById(chatId)) {
            TelegramUserEntity user = new TelegramUserEntity();
            user.setChatId(chatId);
            user.setUserName(userName);
            user.setSubscribed(true);
            user.setLocale(locale);
            userRepository.save(user);

            sendTelegramMessage(chatId, getLocalizedMessage("message.welcome", null, locale));
        }
    }

    private void processSuspendCommand(Long chatId, Locale locale) {
        Optional<TelegramUserEntity> user = userRepository.findById(chatId);
        if (user.isPresent() && user.get().getSubscribed()) {
            user.get().setSubscribed(false);
            userRepository.save(user.get());

            sendTelegramMessage(chatId, getLocalizedMessage("message.events.suspended", null, locale));
        }
    }

    private void processResumeCommand(Long chatId, Locale locale) {
        Optional<TelegramUserEntity> user = userRepository.findById(chatId);
        if (user.isPresent() && !user.get().getSubscribed()) {
            user.get().setSubscribed(true);
            userRepository.save(user.get());

            sendTelegramMessage(chatId, getLocalizedMessage("message.events.resumed", null, locale));
        }
    }
}
