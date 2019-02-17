package com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbMovieReleaseDateDeserializer;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "episodes", "overview")
@EqualsAndHashCode
public class SeasonWrapper {

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("air_date")
    private LocalDate airDate;

    @JsonProperty("episodes")
    private List<EpisodeWrapper> episodes = new ArrayList<>();

    @JsonProperty("name")
    private String name;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("id")
    private Integer id;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("season_number")
    private Integer seasonNumber;
}
