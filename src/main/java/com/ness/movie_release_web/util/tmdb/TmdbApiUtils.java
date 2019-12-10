package com.ness.movie_release_web.util.tmdb;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestOperations;

import java.util.Optional;

import static java.lang.Thread.sleep;

@UtilityClass
@Slf4j
public class TmdbApiUtils {

    public static <T> Optional<T> getTmdbEntity(String url, RestOperations restTemplate, Class<T> type) {

        ResponseEntity<T> response;
        try {
            response = restTemplate.getForEntity(url, type);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get entity url={}, status={}", url, e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return getTmdbEntity(url, restTemplate, type);
            }
            return Optional.empty();
        } catch (Exception e) {
            log.error("Could not get entity url={}", url);
            return Optional.empty();
        }

        return Optional.ofNullable(response.getBody());
    }
}
