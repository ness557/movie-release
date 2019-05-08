package com.ness.movie_release_web.model.wrapper.tmdb.movie.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbMovieReleaseDateDeserializer;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"video", "posterPath", "overview", "backdropPath"})
@EqualsAndHashCode
public class MovieWrapper {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("video")
    private Boolean video;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("vote_average")
    private Long voteAverage;

    @JsonProperty("title")
    private String title;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("genre_ids")
    private List<Long> genreIds = null;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("adult")
    private Boolean adult;

    @JsonProperty("overview")
    private String overview;

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("release_date")
    private LocalDate releaseDate;
}