package com.ness.movie_release_web.model.wrapper.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.wrapper.tmdb.GenreWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.ProductionCompanyWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDateWrapper;
import com.ness.movie_release_web.util.tmdb.TmdbMovieReleaseDateDeserializer;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MovieDetailsWrapper {

    @JsonProperty("adult")
    private Boolean adult;

    @JsonDeserialize(using =TmdbPosterPathDeserializer.class)
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("belongs_to_collection")
    private Object belongsToCollection;

    @JsonProperty("budget")
    private Integer budget;

    @JsonProperty("genres")
    private Set<GenreWrapper> genreWrappers = new HashSet<>(); //used

    @JsonProperty("homepage")
    private String homepage; //used

    @JsonProperty("id")
    private Integer id; //used

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("original_language")
    private String originalLanguage; //used

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("overview")
    private String overview; //used

    @JsonProperty("popularity")
    private Double popularity;

    @JsonDeserialize(using =TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath; //used

    @JsonProperty("production_companies")
    private List<ProductionCompanyWrapper> productionCompanies = new ArrayList<>(); //used

    @JsonProperty("production_countries")
    private List<ProductionCountryWrapper> productionCountries = new ArrayList<>(); //used

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("release_date")
    private LocalDate releaseDate; //not needed

    @JsonProperty("revenue")
    private Integer revenue; //used

    @JsonProperty("runtime")
    private Integer runtime; // minutes used

    @JsonProperty("spoken_languages")
    private List<SpokenLanguageWrapper> spokenLanguageWrappers = new ArrayList<>();

    @JsonProperty("status")
    private String status; //used

    @JsonProperty("tagline")
    private String tagline; //used

    @JsonProperty("title")
    private String title; //used

    @JsonProperty("video")
    private Boolean video;

    @JsonProperty("vote_average")
    private Double voteAverage; //used

    @JsonProperty("vote_count")
    private Integer voteCount;

    private List<ReleaseDateWrapper> releaseDateWrappers = new ArrayList<>(); //used
}
