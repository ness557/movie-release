package com.ness.movie_release_web.dto.tvseries;

import com.ness.movie_release_web.dto.TimeSpentDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbSeasonDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TvSeriesSeasonDto extends TimeSpentDto {

    private boolean subscribed;
    private TmdbTVDetailsDto tvDetailsDto;
    private TmdbSeasonDto seasonDto;
    private Long currentSeason;
    private Long currentEpisode;

}

