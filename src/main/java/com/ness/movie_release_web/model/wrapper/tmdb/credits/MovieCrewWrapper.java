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
@ToString(exclude = {"backdropPath", "posterPath"})
@EqualsAndHashCode
public class MovieCrewWrapper {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("department")
    private String department;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("job")
    private String job;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("popularity")
    private Float popularity;

    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("release_date")
    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    private LocalDate releaseDate;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds = new ArrayList<>();

    @JsonProperty("backdrop_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String backdropPath;

    @JsonProperty("poster_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String posterPath;
}
