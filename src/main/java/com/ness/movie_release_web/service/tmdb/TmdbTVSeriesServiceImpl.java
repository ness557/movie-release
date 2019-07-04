package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.EpisodeDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.SeasonDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.details.TVDetailsDto;
import com.ness.movie_release_web.model.dto.tmdb.tvSeries.search.TVSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

import static java.lang.Thread.sleep;

@Service
@Slf4j
public class TmdbTVSeriesServiceImpl extends Cacheable<TVDetailsDto> implements TmdbTVSeriesService {

    private RestTemplate restTemplate = new RestTemplate();

    private Map<Long, Map<Long, Map<Language, SeasonDto>>> seasonCache = new WeakHashMap<>();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Optional<TVDetailsDto> getTVDetails(Long tmdbId, Language language) {

        Optional<TVDetailsDto> fromCache = getFromCache(tmdbId, language);
        if (fromCache.isPresent()) {
            return fromCache;
        }

        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("append_to_response", "next_episode_to_air,credits,videos");

        ResponseEntity<TVDetailsDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), TVDetailsDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get tv details by id: {}, status: {}", tmdbId, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getTVDetails(tmdbId, language);
            }
            return Optional.empty();
        }

        putToCache(tmdbId, response.getBody(), language);

        return Optional.ofNullable(response.getBody());
    }

    @Override
    public Optional<SeasonDto> getSeasonDetails(Long tmdbId, Long season, Language language) {

        Optional<SeasonDto> seasonFromCache = getSeasonFromCache(tmdbId, season, language);
        if (seasonFromCache.isPresent()) {
            return seasonFromCache;
        }

        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .path("/season/")
                .path(season.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        ResponseEntity<SeasonDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), SeasonDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get tv season details by id: {}, season: {}, status: {}", tmdbId, season, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getSeasonDetails(tmdbId, season, language);
            }
            return Optional.empty();
        }

        if (response.getBody() != null)
            putSeasonToCache(tmdbId, response.getBody(), language);

        return Optional.ofNullable(response.getBody());
    }

    @Override
    public Optional<EpisodeDto> getEpisodeDetails(Long tmdbId, Long season, Long episode, Language language) {
        UriComponentsBuilder UrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .path("/season/")
                .path(season.toString())
                .path("/episode/")
                .path(episode.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        ResponseEntity<EpisodeDto> response;
        try {
            response = restTemplate.getForEntity(UrlBuilder.toUriString(), EpisodeDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not get tv episod details by id: {}, season: {}, episode: {}, status: {}", tmdbId, season, episode, e.getStatusCode().value());

            // if there are too many requests
            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.getEpisodeDetails(tmdbId, season, episode, language);
            }
            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }

    @Override
    public Optional<TVSearchDto> search(String query, Integer page, Long year, Language language) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/tv")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            urlBuilder.queryParam("first_air_date_year", year);

        ResponseEntity<TVSearchDto> response;
        try {
            response = restTemplate.getForEntity(urlBuilder.build(false).toUriString(), TVSearchDto.class);
        } catch (HttpStatusCodeException e) {
            log.error("Could not search for movie: {}, status: {}", query, e.getStatusCode().value());

            if (e.getStatusCode().value() == 429) {
                try {
                    // sleep current thread for 1s
                    sleep(1000);
                } catch (InterruptedException e1) {
                    log.error(e1.getMessage());
                }
                // and try again
                return this.search(query, page, year, language);
            }

            return Optional.empty();
        }
        return Optional.ofNullable(response.getBody());
    }


    private Optional<SeasonDto> getSeasonFromCache(Long id, Long seasonNum, Language language) {
        Map<Long, Map<Language, SeasonDto>> seriesMap = seasonCache.computeIfAbsent(id, k -> new HashMap<>());
        Map<Language, SeasonDto> languageSeasonMap = seriesMap.computeIfAbsent(seasonNum, k -> new WeakHashMap<>());
        return Optional.ofNullable(languageSeasonMap.get(language));
    }

    private void putSeasonToCache(Long id, SeasonDto object, Language language) {
        Map<Long, Map<Language, SeasonDto>> seriesMap = seasonCache.computeIfAbsent(id, k -> new HashMap<>());
        Map<Language, SeasonDto> languageSeasonMap = seriesMap.computeIfAbsent(object.getSeasonNumber(), k -> new WeakHashMap<>());
        languageSeasonMap.put(language, object);
    }
}
