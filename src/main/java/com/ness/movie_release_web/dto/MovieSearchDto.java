package com.ness.movie_release_web.dto;

import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieSearchDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class MovieSearchDto {
    private Map<TmdbMovieDto, Boolean> movieSearchDtoMap;
    private Long total;
}
