package com.ness.movie_release_web;

import com.ness.movie_release_web.model.wrapper.tmdb.movie.discover.SortBy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieReleaseWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MovieReleaseWebApplication.class, args);
    }
}
