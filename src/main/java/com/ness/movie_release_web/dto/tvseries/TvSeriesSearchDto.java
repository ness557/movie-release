package com.ness.movie_release_web.dto.tvseries;

import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.search.TmdbTVSearchDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class TvSeriesSearchDto {
    private Map<TmdbTVDto, Boolean> seriesSearchDtoMap;
    private Long total;
}
