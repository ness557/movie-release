package com.ness.movie_release_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class MovieReleaseWebApplication {
    public static void main(String[] args) {
        // telegram api init
        ApiContextInitializer.init();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Kiev"));
        SpringApplication.run(MovieReleaseWebApplication.class, args);
    }
}
