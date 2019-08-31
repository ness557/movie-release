package com.ness.movie_release_web.dto.movie;

import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieDto;
import com.ness.movie_release_web.dto.tmdb.people.TmdbPeopleDto;
import com.ness.movie_release_web.repository.MovieSortBy;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class MovieDiscoverDto {
    private Map<TmdbMovieDto, Boolean> tmdbMovieDtoMap;
    private Long totalPages;
    private List<TmdbProductionCompanyDto> companies;
    private List<TmdbPeopleDto> people;
    private List<TmdbGenreDto> genres;
}
