package com.ness.movie_release_web.service;

import com.ness.movie_release_web.model.wrapper.tmdbReleaseDates.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
public class TmdbDatesServiceImpl implements TmdbDatesService {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;


    public List<ReleaseDate> getReleaseDates(String imdbId, ReleaseType... releaseTypes) {

        if (releaseTypes.length < 1) {
            logger.error("No releaseTypes");
            return emptyList();
        }

        List<ReleaseDateResult> releaseDates = getTmdbReleaseDates(imdbId);

        List<ReleaseDate> result = new ArrayList<>();

        releaseDates.forEach(e -> {
            List<ReleaseDate> filteredByReleaseTypes =
                    e.getReleaseDates().stream()
                            .filter(rd -> Arrays.stream(releaseTypes)
                                    .collect(toList())
                                    .contains(rd.getReleaseType()))
                            .collect(toList());

            result.addAll(filteredByReleaseTypes);
        });
        return result.stream().sorted().filter(distinctByKey(ReleaseDate::getReleaseType)).collect(toList());
    }

    @Override
    public List<ReleaseDate> getReleaseDates(String imdbId) {

        List<ReleaseDateResult> releaseDates = getTmdbReleaseDates(imdbId);

        List<ReleaseDate> result = new ArrayList<>();

        releaseDates.forEach(e -> result.addAll(e.getReleaseDates()));

        return result.stream().sorted().filter(distinctByKey(ReleaseDate::getReleaseType)).collect(toList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> ke) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(ke.apply(t), Boolean.TRUE) == null;
    }

    private List<ReleaseDateResult> getTmdbReleaseDates(String imdbId) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "find/")
                .path(imdbId)
                .queryParam("external_source", "imdb_id")
                .queryParam("api_key", apikey);
        ResponseEntity<MovieResultList> response = restTemplate.getForEntity(movieBuilder.toUriString(), MovieResultList.class);

        if (response.getStatusCode().value() != 200) {
            logger.error("Could not get movie: {}", response.getStatusCode());
            return emptyList();
        }

        Integer tmdbId = null;
        try {
            tmdbId = response.getBody().getMovieResultList().get(0).getId();
        } catch (Exception e) {
            logger.error("Could not get movie: {}", e.getMessage());
            return emptyList();
        }

        UriComponentsBuilder releasesBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString()).path("/release_dates")
                .queryParam("api_key", apikey);
        ResponseEntity<ReleaseDateResultList> releaseResponse = restTemplate.getForEntity(releasesBuilder.toUriString(), ReleaseDateResultList.class);

        if (releaseResponse.getStatusCode().value() != 200) {
            logger.error("Could not get release dates: {}", response.getStatusCode());
            return emptyList();
        }

        return releaseResponse.getBody().getReleaseDates();
    }

}
