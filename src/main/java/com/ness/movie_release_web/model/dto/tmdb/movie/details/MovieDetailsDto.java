package com.ness.movie_release_web.model.dto.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.model.dto.tmdb.GenreDto;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.ProductionCompanyDto;
import com.ness.movie_release_web.model.dto.tmdb.VideosDto;
import com.ness.movie_release_web.model.dto.tmdb.credits.PeopleCreditsDto;
import com.ness.movie_release_web.model.dto.tmdb.releaseDates.ReleaseDate;
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
@ToString(exclude = {"backdropPath",
                     "genres",
                     "posterPath",
                     "overview",
                     "productionCompanies",
                     "productionCountries",
                     "spokenLanguages",
                     "credits"})
@EqualsAndHashCode
public class MovieDetailsDto {

    @JsonProperty("adult")
    private Boolean adult;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("belongs_to_collection")
    private Object belongsToCollection;

    @JsonProperty("budget")
    private Long budget;

    @JsonProperty("genres")
    private Set<GenreDto> genres = new HashSet<>();

    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("id")
    private Long id;

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

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("production_companies")
    private List<ProductionCompanyDto> productionCompanies = new ArrayList<>();

    @JsonProperty("production_countries")
    private List<ProductionCountryDto> productionCountries = new ArrayList<>();

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("release_date")
    private LocalDate releaseDate; //not needed

    @JsonProperty("revenue")
    private Long revenue;

    @JsonProperty("runtime")
    private Long runtime; // minutes used

    @JsonProperty("spoken_languages")
    private List<SpokenLanguageDro> spokenLanguages = new ArrayList<>();

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
    private Long voteCount;

    @JsonProperty("credits")
    private PeopleCreditsDto credits = new PeopleCreditsDto();

    @JsonProperty("videos")
    private VideosDto videos;

    private List<ReleaseDate> releaseDates = new ArrayList<>();

    public static MovieDetailsDto of(Film film, Language language) {
        MovieDetailsDto dto = new MovieDetailsDto();
        dto.setId(film.getId());
        switch (language) {
            case ru:
                dto.setTitle(film.getNameRu());
                break;
            case en:
                dto.setTitle(film.getNameEn());
                break;
            default:
                dto.setTitle(film.getNameRu());
                break;
        }
        dto.setReleaseDate(film.getReleaseDate());
        return dto;
    }
}
