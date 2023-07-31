package com.k4ktyc.dockertelegramnotifier.telegram.service;

import com.k4ktyc.dockertelegramnotifier.config.TelegramBotConfig;
import com.k4ktyc.dockertelegramnotifier.docker.model.DockerEventEntity;
import com.k4ktyc.dockertelegramnotifier.docker.service.DockerEventService;
import com.k4ktyc.dockertelegramnotifier.telegram.model.TelegramUserEntity;
import com.k4ktyc.dockertelegramnotifier.telegram.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramBotConfig botConfig;
    private final TelegramUserRepository userRepository;

    private final DockerEventService dockerEventService;

    private static final String WELCOME_MESSAGE = "Receiving events from Docker was started";
    private static final String PAUSED_MESSAGE = "Receiving events from Docker was paused";
    private static final String RESUMED_MESSAGE = "Receiving events from Docker was resumed";


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
                switch (text) {
                    case "/start" -> processStartCommand(chatId, userName);
                    case "/pause" -> processPauseCommand(chatId);
                    case "/resume" -> processResumeCommand(chatId);
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

    public void sendMessageToSubscribedUsers(String messageToSend) {
        userRepository.findAllChatIdsSubscribed()
                .forEach(chat -> sendMessage(chat, messageToSend));
    }


    private void processStartCommand(Long chatId, String userName) {
        if (!userRepository.existsById(chatId)) {
            TelegramUserEntity user = new TelegramUserEntity();
            user.setChatId(chatId);
            user.setUserName(userName);
            user.setReceiveUpdates(true);
            userRepository.save(user);

            sendMessage(chatId, WELCOME_MESSAGE);
        }
    }

    private void processPauseCommand(Long chatId) {
        Optional<TelegramUserEntity> user = userRepository.findById(chatId);
        if (user.isPresent() && user.get().getReceiveUpdates()) {
            user.get().setReceiveUpdates(false);
            userRepository.save(user.get());

            sendMessage(chatId, PAUSED_MESSAGE);
        }
    }

    private void processResumeCommand(Long chatId) {
        Optional<TelegramUserEntity> user = userRepository.findById(chatId);
        if (user.isPresent() && !user.get().getReceiveUpdates()) {
            user.get().setReceiveUpdates(true);
            userRepository.save(user.get());

            sendMessage(chatId, RESUMED_MESSAGE);
        }
    }
}
