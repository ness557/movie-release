package com.ness.movie_release_web.dto.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.TmdbVideosDto;
import com.ness.movie_release_web.dto.tmdb.credits.TmdbPeopleCreditsDto;
import com.ness.movie_release_web.dto.tmdb.releaseDates.TmdbReleaseDate;
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
public class TmdbMovieDetailsDto {

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
    private Set<TmdbGenreDto> genres = new HashSet<>();

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
    private List<TmdbProductionCompanyDto> productionCompanies = new ArrayList<>();

    @JsonProperty("production_countries")
    private List<TmdbProductionCountryDto> productionCountries = new ArrayList<>();

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("release_date")
    private LocalDate releaseDate; //not needed

    @JsonProperty("revenue")
    private Long revenue;

    @JsonProperty("runtime")
    private Long runtime; // minutes used

    @JsonProperty("spoken_languages")
    private List<TmdbSpokenLanguageDro> spokenLanguages = new ArrayList<>();

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
    private TmdbPeopleCreditsDto credits = new TmdbPeopleCreditsDto();

    @JsonProperty("videos")
    private TmdbVideosDto videos;

    private List<TmdbReleaseDate> releaseDates = new ArrayList<>();

    public static TmdbMovieDetailsDto of(Film film, Language language) {
        TmdbMovieDetailsDto dto = new TmdbMovieDetailsDto();
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
