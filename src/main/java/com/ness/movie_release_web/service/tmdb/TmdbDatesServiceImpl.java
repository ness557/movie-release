package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.releaseDates.ReleaseType;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDateResultDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDateResultListDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbDatesServiceImpl implements TmdbDatesService {

    private final TmdbExternalIdService tmdbExternalIdService;
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getReleaseDates")
    public List<TmdbReleaseDate> getReleaseDates(String imdbId, ReleaseType... releaseTypes) {

        if (releaseTypes.length < 1) {
            log.error("No releaseTypes");
            return emptyList();
        }

        Long tmdbId = tmdbExternalIdService.getTmdbIdByImdbId(imdbId);
        List<TmdbReleaseDateResultDto> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctAndFilterReleadeDates(releaseDates, releaseTypes);
    }

    @Override
    @Cacheable("getReleaseDates")
    public List<TmdbReleaseDate> getReleaseDates(String imdbId) {

        Long tmdbId = tmdbExternalIdService.getTmdbIdByImdbId(imdbId);
        List<TmdbReleaseDateResultDto> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctReleaseDates(releaseDates);
    }

    @Cacheable("getReleaseDates")
    @Override
    public List<TmdbReleaseDate> getReleaseDates(Long tmdbId, ReleaseType... releaseTypes) {

        if (releaseTypes.length < 1) {
            log.error("No releaseTypes");
            return emptyList();
        }

        List<TmdbReleaseDateResultDto> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctAndFilterReleadeDates(releaseDates, releaseTypes);
    }

    @Override
    @Cacheable("getReleaseDates")
    public List<TmdbReleaseDate> getReleaseDates(Long tmdbId) {

        List<TmdbReleaseDateResultDto> releaseDates = getReleaseDatesByTmdbId(tmdbId);

        return distinctReleaseDates(releaseDates);
    }

    private List<TmdbReleaseDate> distinctReleaseDates(List<TmdbReleaseDateResultDto> releaseDates) {
        List<TmdbReleaseDate> result = new ArrayList<>();

        releaseDates.forEach(e -> result.addAll(e.getReleaseDates()));

        return result.stream().sorted().filter(distinctByKey(TmdbReleaseDate::getReleaseType)).collect(toList());
    }

    private List<TmdbReleaseDate> distinctAndFilterReleadeDates(List<TmdbReleaseDateResultDto> releaseDates, ReleaseType[] releaseTypes) {
        List<TmdbReleaseDate> result = new ArrayList<>();

        releaseDates.forEach(e -> {
            List<TmdbReleaseDate> filteredByReleaseTypes =
                    e.getReleaseDates().stream()
                            .filter(rd -> Arrays.stream(releaseTypes)
                                    .collect(toList())
                                    .contains(rd.getReleaseType()))
                            .collect(toList());

            result.addAll(filteredByReleaseTypes);
        });
        return result.stream().sorted().filter(distinctByKey(TmdbReleaseDate::getReleaseType)).collect(toList());
    }

    private List<TmdbReleaseDateResultDto> getReleaseDatesByTmdbId(Long tmdbId) {
        UriComponentsBuilder releasesBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString()).path("/release_dates")
                .queryParam("api_key", apikey);

        return getTmdbEntity(releasesBuilder.toUriString(), restTemplate, TmdbReleaseDateResultListDto.class)
                .map(TmdbReleaseDateResultListDto::getReleaseDates).orElse(Collections.emptyList());
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> ke) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(ke.apply(t), Boolean.TRUE) == null;
    }
}
