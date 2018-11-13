package com.ness.movie_release_web;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.service.tmdb.TmdbTVSeriesService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MovieReleaseWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieReleaseWebApplication.class, args);
    }
}
