package com.ness.movie_release_web.model.dto.tmdb.credits;

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
@ToString(exclude = {"backdropPath", "posterPath", "overview"})
@EqualsAndHashCode
public class TVCrewDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("department")
    private String department;

    @JsonProperty("origin_country")
    private List<String> originCountries = new ArrayList<>();

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("job")
    private String job;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("popularity")
    private Float popularity;

    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("first_air_date")
    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    private LocalDate firstAirDate;

    @JsonProperty("genre_ids")
    private List<Long> genreIds = new ArrayList<>();

    @JsonProperty("backdrop_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String backdropPath;

    @JsonProperty("poster_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String posterPath;
}
