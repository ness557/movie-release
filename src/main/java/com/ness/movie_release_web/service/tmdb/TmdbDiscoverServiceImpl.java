package com.ness.movie_release_web.service.tmdb;

import com.ness.movie_release_web.dto.tmdb.movie.discover.TmdbDiscoverSearchCriteria;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.ness.movie_release_web.util.tmdb.TmdbApiUtils.getTmdbEntity;
import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class TmdbDiscoverServiceImpl implements TmdbDiscoverService {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${tmdbapi.apikey}")
    private String apikey;

    @Value("${tmdbapi.url}")
    private String url;

    @Override
    public Optional<TmdbMovieSearchDto> discoverMovie(TmdbDiscoverSearchCriteria criteria) {

        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "discover/movie")
                .queryParam("api_key", apikey)
                .queryParam("sort_by", criteria.getSortBy().getSearchString())
                .queryParam("vote_average.gte", criteria.getVoteAverageMin())
                .queryParam("vote_average.lte", criteria.getVoteAverageMax())
                .queryParam("page", criteria.getPage())
                .queryParam("language", criteria.getLanguage());

        if (criteria.getReleaseDateMin() != null)
            movieBuilder.queryParam("release_date.gte",
                    criteria.getReleaseDateMin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (criteria.getReleaseDateMax() != null)
            movieBuilder.queryParam("release_date.lte",
                    criteria.getReleaseDateMax().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (!criteria.getGenres().isEmpty())
            movieBuilder.queryParam("with_genres",
                    StringUtils.join(criteria.getGenres(), criteria.getGenresAnd() ? "," : "|"));

        if (!criteria.getReleaseTypes().isEmpty())
            movieBuilder.queryParam("with_release_type",
                    StringUtils.join(criteria.getReleaseTypes().stream().map(Enum::ordinal).collect(toList()), criteria.getReleaseTypeAnd() ? "," : "|"));

        if (!criteria.getCompanies().isEmpty())
            movieBuilder.queryParam("with_companies",
                    StringUtils.join(criteria.getCompanies(), criteria.getCompaniesAnd() ? "," : "|"));

        if (!criteria.getPeople().isEmpty())
            movieBuilder.queryParam("with_people",
                    StringUtils.join(criteria.getPeople(), criteria.getPeopleAnd() ? "," : "|"));


        return getTmdbEntity(movieBuilder.build(false).toUriString(), restTemplate, TmdbMovieSearchDto.class);
    }

    @Override
    public Optional<TmdbTVSearchDto> discoverSeries(TmdbDiscoverSearchCriteria criteria) {
        UriComponentsBuilder movieBuilder = UriComponentsBuilder.fromHttpUrl(url + "discover/tv")
                .queryParam("api_key", apikey)
                .queryParam("sort_by", criteria.getSortBy().getSearchString())
                .queryParam("vote_average.gte", criteria.getVoteAverageMin())
                .queryParam("vote_average.lte", criteria.getVoteAverageMax())
                .queryParam("page", criteria.getPage())
                .queryParam("language", criteria.getLanguage());

        if (criteria.getReleaseDateMin() != null)
            movieBuilder.queryParam("first_air_date.gte",
                    criteria.getReleaseDateMin().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (criteria.getReleaseDateMax() != null)
            movieBuilder.queryParam("first_air_date.lte",
                    criteria.getReleaseDateMax().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        if (!criteria.getGenres().isEmpty())
            movieBuilder.queryParam("with_genres",
                    StringUtils.join(criteria.getGenres(), criteria.getGenresAnd() ? "," : "|"));

        if (!criteria.getNetworks().isEmpty())
            movieBuilder.queryParam("with_networks",
                    StringUtils.join(criteria.getNetworks(), criteria.getNetworksAnd() ? "," : "|"));

        if (!criteria.getCompanies().isEmpty())
            movieBuilder.queryParam("with_companies",
                    StringUtils.join(criteria.getCompanies(), criteria.getCompaniesAnd() ? "," : "|"));

        return getTmdbEntity(movieBuilder.build(false).toUriString(), restTemplate, TmdbTVSearchDto.class);
    }
}
