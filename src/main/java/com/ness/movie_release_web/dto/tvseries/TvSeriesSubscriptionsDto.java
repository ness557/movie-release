package com.ness.movie_release_web.dto.tvseries;

import com.ness.movie_release_web.dto.tmdb.movie.details.TmdbMovieDetailsDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import com.ness.movie_release_web.repository.MovieSortBy;
import com.ness.movie_release_web.repository.TVSeriesSortBy;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class TvSeriesSubscriptionsDto {
    private TVSeriesSortBy sortBy;
    private Boolean botInitialized;
    private List<TmdbTVDetailsDto> seriesDetailsDtos;
    private Long totalPages;
}
