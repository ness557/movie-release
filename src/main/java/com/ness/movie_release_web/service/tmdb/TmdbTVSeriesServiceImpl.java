package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbEpisodeDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;

@Service
@Slf4j
@RequiredArgsConstructor
public class TmdbTVSeriesServiceImpl implements TmdbTVSeriesService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getTVDetails")
    public Optional<TmdbTVDetailsDto> getTVDetails(Long tmdbId, Language language) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("append_to_response", "next_episode_to_air,credits,videos");

        return getTmdbEntity(urlBuilder.toUriString(), restTemplate, TmdbTVDetailsDto.class);
    }

    @Override
    @Cacheable("getSeasonDetails")
    public Optional<TmdbSeasonDto> getSeasonDetails(Long tmdbId, Long season, Language language) {

        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .path("/season/")
                .path(season.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        return getTmdbEntity(urlBuilder.toUriString(), restTemplate, TmdbSeasonDto.class);
    }

    @Override
    @Cacheable("getEpisodeDetails")
    public Optional<TmdbEpisodeDto> getEpisodeDetails(Long tmdbId, Long season, Long episode, Language language) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "tv/")
                .path(tmdbId.toString())
                .path("/season/")
                .path(season.toString())
                .path("/episode/")
                .path(episode.toString())
                .queryParam("api_key", apikey)
                .queryParam("language", language.name());

        return getTmdbEntity(urlBuilder.toUriString(), restTemplate, TmdbEpisodeDto.class);
    }

    @Override
    @Cacheable("searchForTvSeries")
    public Optional<TmdbTVSearchDto> search(String query, Integer page, Long year, Language language) {
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/tv")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            urlBuilder.queryParam("first_air_date_year", year);

        return getTmdbEntity(urlBuilder.toUriString(), restTemplate, TmdbTVSearchDto.class);
    }
}
