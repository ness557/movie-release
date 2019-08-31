package com.ness.movie_release_web.dto.tvseries;

import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.movie.search.TmdbMovieDto;
import com.ness.movie_release_web.dto.tmdb.people.TmdbPeopleDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class TvSeriesDiscoverDto {
    private Map<TmdbTVDto, Boolean> seriesSearchDtoMap;
    private Long totalPages;
    private List<TmdbProductionCompanyDto> companies;
    private List<TmdbProductionCompanyDto> networks;
    private List<TmdbGenreDto> genres;
}
