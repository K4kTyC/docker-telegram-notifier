package com.k4ktyc.dockertelegramnotifier.telegram.service;

import com.k4ktyc.dockertelegramnotifier.config.TelegramBotConfig;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import com.k4ktyc.dockertelegramnotifier.telegram.model.TelegramUserEntity;
import com.k4ktyc.dockertelegramnotifier.telegram.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotConfig botConfig;
    private final TelegramUserRepository userRepository;


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
            String userName = message.getChat().getUserName();
            if (botConfig.getAllowedUserNames().contains(userName)) {
                Long chatId = message.getChatId();
                String text = message.getText();
                if ("/start".equals(text)) {
                    processStartCommand(chatId, userName);
                }
            }
        }
    }

    @EventListener
    public void onDockerEventReceived(DockerEventEntity eventEntity) {
        String text = eventEntity.getEventType() + " " + eventEntity.getAction();
        sendMessageToAllUsers(text);
    }

    public void sendMessageToAllUsers(String messageToSend) {
        userRepository.findAllChatIds()
                .forEach(chat -> sendMessage(chat, messageToSend));
    }

    private void processStartCommand(Long chatId, String userName) {
        if (userRepository.findById(chatId).isEmpty()) {
            TelegramUserEntity user = new TelegramUserEntity();
            user.setChatId(chatId);
            user.setUserName(userName);

            userRepository.save(user);

            sendMessage(chatId, "hello");
        }
    }

    private void sendMessage(Long chatId, String messageToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
