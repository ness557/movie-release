package com.ness.movie_release_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class MovieReleaseWebApplication {
    public static void main(String[] args) {
        // telegram api init
        ApiContextInitializer.init();
        TelegramBotsApi botApi = new TelegramBotsApi();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kiev"));
        ConfigurableApplicationContext run = SpringApplication.run(MovieReleaseWebApplication.class, args);
        try {
            botApi.registerBot(run.getBean(TelegramLongPollingBot.class));
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
