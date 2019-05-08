package com.ness.movie_release_web.model.wrapper.tmdb.externalId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"backdropPath", "posterPath", "video"})
public class MovieResultWrapper {

    @JsonProperty("adult")
    private Boolean adult;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("genre_ids")
    private List<Long> genreIds = null;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("popularity")
    private Double popularity;

    @JsonProperty("title")
    private String title;

    @JsonProperty("video")
    private Boolean video;

    @JsonProperty("vote_average")
    private Double voteAverage;

    @JsonProperty("vote_count")
    private Long voteCount;
}
