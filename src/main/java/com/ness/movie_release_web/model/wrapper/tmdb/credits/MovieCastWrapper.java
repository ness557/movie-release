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
@ToString(exclude = {"backdropPath", "overview"})
@EqualsAndHashCode
public class MovieCastWrapper {

    @JsonProperty("popularity")
    private Float popularity;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("vote_average")
    private Long voteAverage;

    @JsonProperty("genre_ids")
    private List<Long> genreIds = new ArrayList<>();

    @JsonProperty("title")
    private String title;

    @JsonProperty("release_date")
    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    private LocalDate releaseDate;

    @JsonProperty("poster_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String posterPath;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

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
