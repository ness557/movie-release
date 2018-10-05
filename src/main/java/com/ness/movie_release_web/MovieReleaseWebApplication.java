package com.ness.movie_release_web;

import com.ness.movie_release_web.service.FilmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MovieReleaseWebApplication {

    private static Logger logger = LoggerFactory.getLogger("====>>> TEST");

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(MovieReleaseWebApplication.class, args);
//        NotificationServiceImpl service = context.getBean(NotificationServiceImpl.class);
        FilmService service = context.getBean(FilmService.class);
        logger.error(service.getAll().toString());
    }
}
