package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDateWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDateResultWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDateResultListWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.Thread.sleep;
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

    @Autowired
    private ExternalIdService externalIdService;

    @Override
    public List<ReleaseDateWrapper> getReleaseDates(String imdbId, ReleaseType... releaseTypes) {

        if (releaseTypes.length < 1) {
            logger.error("No releaseTypes");
            return emptyList();
        }

        Integer tmdbId = externalIdService.getTmdbIdByImdbId(imdbId);
        List<ReleaseDateResultWrapper> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctAndFilterReleadeDates(releaseDates, releaseTypes);
    }

    @Override
    public List<ReleaseDateWrapper> getReleaseDates(String imdbId) {

        Integer tmdbId = externalIdService.getTmdbIdByImdbId(imdbId);
        List<ReleaseDateResultWrapper> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctReleaseDates(releaseDates);
    }

    public List<ReleaseDateWrapper> getReleaseDates(Integer tmdbId, ReleaseType... releaseTypes) {

        if (releaseTypes.length < 1) {
            logger.error("No releaseTypes");
            return emptyList();
        }

        List<ReleaseDateResultWrapper> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctAndFilterReleadeDates(releaseDates, releaseTypes);
    }


    public List<ReleaseDateWrapper> getReleaseDates(Integer tmdbId) {

        List<ReleaseDateResultWrapper> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctReleaseDates(releaseDates);
    }

    private List<ReleaseDateWrapper> distinctReleaseDates(List<ReleaseDateResultWrapper> releaseDates) {
        List<ReleaseDateWrapper> result = new ArrayList<>();

        releaseDates.forEach(e -> result.addAll(e.getReleaseDateWrappers()));

        return result.stream().sorted().filter(distinctByKey(ReleaseDateWrapper::getReleaseType)).collect(toList());
    }

    private List<ReleaseDateWrapper> distinctAndFilterReleadeDates(List<ReleaseDateResultWrapper> releaseDates, ReleaseType[] releaseTypes) {
        List<ReleaseDateWrapper> result = new ArrayList<>();

        releaseDates.forEach(e -> {
            List<ReleaseDateWrapper> filteredByReleaseTypes =
                    e.getReleaseDateWrappers().stream()
                            .filter(rd -> Arrays.stream(releaseTypes)
                                    .collect(toList())
                                    .contains(rd.getReleaseType()))
                            .collect(toList());

            result.addAll(filteredByReleaseTypes);
        });
        return result.stream().sorted().filter(distinctByKey(ReleaseDateWrapper::getReleaseType)).collect(toList());
    }

    private List<ReleaseDateResultWrapper> getReleaseDatesByTmdbId(Integer tmdbId) {
        UriComponentsBuilder releasesBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString()).path("/release_dates")
                .queryParam("api_key", apikey);
        ResponseEntity<ReleaseDateResultListWrapper> releaseResponse = null;
        try {
            releaseResponse = restTemplate.getForEntity(releasesBuilder.toUriString(), ReleaseDateResultListWrapper.class);
        } catch (HttpStatusCodeException e) {
            logger.error("Could not get release dates: {}", e.getMessage());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    logger.error(e1.getMessage());
                }
                // and try again
                return this.getReleaseDatesByTmdbId(tmdbId);
            }

            return emptyList();
        }

        return releaseResponse.getBody().getReleaseDates();
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> ke) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(ke.apply(t), Boolean.TRUE) == null;
    }
}
