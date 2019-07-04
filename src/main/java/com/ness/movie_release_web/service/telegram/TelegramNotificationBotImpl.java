package com.ness.movie_release_web.service.telegram;

import com.ness.movie_release_web.model.User;
import com.ness.movie_release_web.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramNotificationBotImpl extends TelegramLongPollingBot implements TelegramNotificationBot{

    @Value("${telegram.name}")
    private String botName;

    @Value("${telegram.token}")
    private String token;

    @Value("${telegram.webInterfaceLink}")
    private String appLink;

    @Autowired
    private UserService userService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

            logger.info("Notification {} sent", photo.toString());
        } catch (TelegramApiException e) {
            logger.error("Could not sent telegram message: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendNotify(String resultText, Long chatId) {
        SendMessage message = new SendMessage();
        try {
            execute(message.setChatId(chatId)
                    .setText(resultText)
                    .setParseMode(ParseMode.MARKDOWN));

            logger.info("Password restore message sent={} ", message.toString());
        } catch (TelegramApiException e) {
            logger.error("Could not sent telegram message: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        String telegramUserName = message.getFrom().getUserName();
        telegramUserName = "@" + StringUtils.lowerCase(telegramUserName);
        User user = userService.findByTelegramId(telegramUserName);

        if (user.getTelegramChatId() == null) {
            user.setTelegramChatId(message.getChatId());
            userService.save(user);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("You have been registered");
            try {
                execute(sendMessage);
                logger.info("User {} registered", telegramUserName);
            } catch (TelegramApiException e) {
                logger.error("Could register telegram user: {}", e.getMessage());
            }
        }
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
