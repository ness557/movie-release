package com.ness.movie_release_web.service.telegram;

public interface TelegramNotificationBot {
    void sendNotify(String resultText, Long telegramChatId, String posterPath);
}
