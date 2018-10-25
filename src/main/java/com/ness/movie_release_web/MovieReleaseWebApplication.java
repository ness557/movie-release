package com.ness.movie_release_web;

import com.ness.movie_release_web.service.TmdbDatesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MovieReleaseWebApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MovieReleaseWebApplication.class, args);

        System.out.println(context.getBean(TmdbDatesService.class).getReleaseDates("tt1392190"));
    }
}
