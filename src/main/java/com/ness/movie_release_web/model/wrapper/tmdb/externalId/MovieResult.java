package com.ness.movie_release_web.model.wrapper.tmdb.externalId;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MovieResult {

    @JsonProperty("adult")
    private Boolean adult;
    @JsonProperty("backdrop_path")
    private Object backdropPath;
    @JsonProperty("genre_ids")
    private List<Integer> genreIds = null;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("original_language")
    private String originalLanguage;
    @JsonProperty("original_title")
    private String originalTitle;
    @JsonProperty("overview")
    private String overview;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("poster_path")
    private Object posterPath;
    @JsonProperty("popularity")
    private Double popularity;
    @JsonProperty("title")
    private String title;
    @JsonProperty("video")
    private Boolean video;
    @JsonProperty("vote_average")
    private Double voteAverage;
    @JsonProperty("vote_count")
    private Integer voteCount;
}
