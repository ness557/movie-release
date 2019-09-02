package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
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
@RequiredArgsConstructor
@Slf4j
public class TmdbMovieServiceImpl implements TmdbMovieService {

    private RestTemplate restTemplate = new RestTemplate();
    private final TmdbDatesService releaseDatesService;

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    @Cacheable("getMovieDetails")
    public Optional<TmdbMovieDetailsDto> getMovieDetails(Long tmdbId, Language language) {

        UriComponentsBuilder movieUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "movie/")
                .path(tmdbId.toString())
                .queryParam("api_key", apikey)
                .queryParam("append_to_response", "credits,videos")
                .queryParam("language", language.name());

        return getTmdbEntity(movieUrlBuilder.toUriString(), restTemplate, TmdbMovieDetailsDto.class)
                .map(dto -> {
                    dto.setReleaseDates(releaseDatesService.getReleaseDates(dto.getId()));
                    return dto;
                });
    }

    @Override
    @Cacheable("searchForMovies")
    public Optional<TmdbMovieSearchDto> searchForMovies(String query, Long page, Long year, Language language) {
        UriComponentsBuilder searchUrlBuilder = UriComponentsBuilder.fromHttpUrl(url + "search/movie/")
                .queryParam("api_key", apikey)
                .queryParam("language", language.name())
                .queryParam("query", query)
                .queryParam("page", page == null ? 1 : page);

        if (year != null)
            searchUrlBuilder.queryParam("year", year);

        return getTmdbEntity(searchUrlBuilder.build(false).toUriString(), restTemplate, TmdbMovieSearchDto.class);
    }
}
