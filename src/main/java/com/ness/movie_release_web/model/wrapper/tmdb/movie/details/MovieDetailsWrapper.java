package com.ness.movie_release_web.model.wrapper.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.wrapper.tmdb.GenreWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import com.ness.movie_release_web.model.wrapper.tmdb.ProductionCompanyWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.Videos;
import com.ness.movie_release_web.model.wrapper.tmdb.credits.PeopleCreditsWrapper;
import com.ness.movie_release_web.model.wrapper.tmdb.releaseDates.ReleaseDate;
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
    private Set<GenreWrapper> genreWrappers = new HashSet<>(); 

    @JsonProperty("homepage")
    private String homepage; 

    @JsonProperty("id")
    private Integer id; 

    @JsonProperty("imdb_id")
    private String imdbId;

    @JsonProperty("original_language")
    private String originalLanguage; 

    @JsonProperty("original_title")
    private String originalTitle;

    @JsonProperty("overview")
    private String overview; 

    @JsonProperty("popularity")
    private Double popularity;

    @JsonDeserialize(using =TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath; 

    @JsonProperty("production_companies")
    private List<ProductionCompanyWrapper> productionCompanies = new ArrayList<>(); 

    @JsonProperty("production_countries")
    private List<ProductionCountryWrapper> productionCountries = new ArrayList<>(); 

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("release_date")
    private LocalDate releaseDate; //not needed

    @JsonProperty("revenue")
    private Integer revenue; 

    @JsonProperty("runtime")
    private Integer runtime; // minutes used

    @JsonProperty("spoken_languages")
    private List<SpokenLanguageWrapper> spokenLanguageWrappers = new ArrayList<>();

    @JsonProperty("status")
    private Status status; 

    @JsonProperty("tagline")
    private String tagline; 

    @JsonProperty("title")
    private String title; 

    @JsonProperty("video")
    private Boolean video;

    @JsonProperty("vote_average")
    private Double voteAverage; 

    @JsonProperty("vote_count")
    private Integer voteCount;

    @JsonProperty("credits")
    private PeopleCreditsWrapper credits = new PeopleCreditsWrapper();

    @JsonProperty("videos")
    private Videos videos;

    private List<ReleaseDate> releaseDates = new ArrayList<>();

    public static MovieDetailsWrapper of(Film film, Language language){
        MovieDetailsWrapper wrapper = new MovieDetailsWrapper();
        wrapper.setId(film.getId().intValue());
        switch (language) {
            case ru:
                wrapper.setTitle(film.getNameRu());
                break;
            case en:
                wrapper.setTitle(film.getNameRu());
                break;
            default:
                wrapper.setTitle(film.getNameRu());
                break;
        }
        wrapper.setReleaseDate(film.getReleaseDate());
        return wrapper;
    }
}
