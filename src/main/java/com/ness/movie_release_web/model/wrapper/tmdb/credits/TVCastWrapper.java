package com.ness.movie_release_web.model.wrapper.tmdb.credits;

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
@ToString(exclude = {"posterPath", "backdropPath", "overview"})
@EqualsAndHashCode
public class TVCastWrapper {

    @JsonProperty("popularity")
    private Float popularity;

    @JsonProperty("origin_country")
    private List<String> originCountries = new ArrayList<>();

    @JsonProperty("episode_count")
    private Long episodeCount;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("vote_average")
    private Long voteAverage;

    @JsonProperty("genre_ids")
    private List<Long> genreIds = new ArrayList<>();

    @JsonProperty("name")
    private String name;

    @JsonProperty("first_air_date")
    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    private LocalDate firstAirDate;

    @JsonProperty("poster_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String posterPath;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("character")
    private String character;

    @JsonProperty("backdrop_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String backdropPath;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("credit_id")
    private String creditId;
}
