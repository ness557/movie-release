package com.ness.movie_release_web.model.dto.tmdb.tvSeries.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbMovieReleaseDateDeserializer;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class EpisodeDto {

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("air_date")
    private LocalDate airDate;

    @JsonProperty("episode_number")
    private Long episodeNumber;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("production_code")
    private String productionCode;

    @JsonProperty("season_number")
    private Long seasonNumber;

    @JsonProperty("show_id")
    private Long showId;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("still_path")
    private String stillPath;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Long voteCount;
}
