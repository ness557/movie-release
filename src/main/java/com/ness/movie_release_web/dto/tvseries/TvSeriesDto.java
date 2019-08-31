package com.ness.movie_release_web.dto.tvseries;

import com.ness.movie_release_web.dto.TimeSpentDto;
import com.ness.movie_release_web.dto.tmdb.tvSeries.details.TmdbTVDetailsDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TvSeriesDto extends TimeSpentDto {
    private boolean subscribed;
    private TmdbTVDetailsDto tvDetailsDto;
    private Long currentSeason;
    private boolean seasonWatched;
    private boolean lastEpisodeWatched;

}
