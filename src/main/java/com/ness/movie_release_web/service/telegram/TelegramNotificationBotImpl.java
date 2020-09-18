package com.ness.movie_release_web.service.telegram;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationBotImpl extends TelegramLongPollingBot implements TelegramNotificationBot {

    @Value("${telegram.name}")
    private String botName;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.webInterfaceLink}")
    private String appLink;

    private final UserRepository userRepository;

    public void sendNotify(String resultText, Long telegramChatId, String posterPath) {
        // Create send method
        SendPhoto photo = new SendPhoto();
        // Set destination chat id
        photo.setChatId(telegramChatId);
        // Set the photo url as a simple photo
        photo.setPhoto(posterPath);
        photo.setCaption(resultText);
        photo.setParseMode(ParseMode.MARKDOWN);

        try {
            execute(photo);

            log.info("Notification {} sent", photo.toString());
        } catch (TelegramApiException e) {
            log.error("Could not sent telegram message: ", e);
        }
    }

    public void sendNotify(String resultText, Long chatId) {
        SendMessage message = new SendMessage();
        try {
            execute(message.setChatId(chatId)
                    .setText(resultText)
                    .setParseMode(ParseMode.MARKDOWN));

            log.info("Password restore message sent={} ", message.toString());
        } catch (TelegramApiException e) {
            log.error("Could not sent telegram message: ", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        String telegramUserName = message.getFrom().getUserName();
        telegramUserName = "@" + StringUtils.lowerCase(telegramUserName);
        User user = userRepository.findByTelegramId(telegramUserName);

        if (user.getTelegramChatId() == null) {
            user.setTelegramChatId(message.getChatId());
            userRepository.save(user);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("You have been registered");
            try {
                execute(sendMessage);
                log.info("User {} registered", telegramUserName);
            } catch (TelegramApiException e) {
                log.error("Could register telegram user: {}", e.getMessage());
            }
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        updates.forEach(this::onUpdateReceived);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
