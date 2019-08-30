package com.ness.movie_release_web.dto.tmdb.tvSeries.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.dto.tmdb.TmdbGenreDto;
import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.dto.tmdb.TmdbProductionCompanyDto;
import com.ness.movie_release_web.dto.tmdb.TmdbVideosDto;
import com.ness.movie_release_web.dto.tmdb.credits.TmdbPeopleCreditsDto;
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
@ToString(exclude = {"networks", "productionCompanies", "seasons", "overview"})
@EqualsAndHashCode
public class TmdbTVDetailsDto {

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("episode_run_time")
    private List<Long> episodeRunTime = new ArrayList<>();

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("first_air_date")
    private LocalDate firstAirDate;

    @JsonProperty("genres")
    private List<TmdbGenreDto> genres = new ArrayList<>();

    @JsonProperty("homepage")
    private String homepage;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("in_production")
    private Boolean inProduction;

    @JsonProperty("languages")
    private List<String> languages = new ArrayList<>();

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("last_air_date")
    private LocalDate lastAirDate;

    @JsonProperty("last_episode_to_air")
    private TmdbEpisodeDto lastEpisodeToAir;

    @JsonProperty("name")
    private String name;

    @JsonProperty("next_episode_to_air")
    private TmdbEpisodeDto nextEpisodeToAir;

    @JsonProperty("networks")
    private List<TmdbProductionCompanyDto> networks = new ArrayList<>();

    @JsonProperty("number_of_episodes")
    private Long numberOfEpisodes;

    @JsonProperty("number_of_seasons")
    private Long numberOfSeasons;

    @JsonProperty("origin_country")
    private List<String> originCountry = new ArrayList<>();

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("overview")
    private String overview;

    @JsonProperty("popularity")
    private Float popularity;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("production_companies")
    private List<TmdbProductionCompanyDto> productionCompanies = new ArrayList<>();

    @JsonProperty("seasons")
    private List<TmdbSeasonDto> seasons = new ArrayList<>();

    @JsonProperty("status")
    private Status status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("videos")
    private TmdbVideosDto videos;

    @JsonProperty("credits")
    private TmdbPeopleCreditsDto credits = new TmdbPeopleCreditsDto();

    public static TmdbTVDetailsDto of(TVSeries tvSeries, Language language) {
        TmdbTVDetailsDto dto = new TmdbTVDetailsDto();
        dto.setId(tvSeries.getId());
        switch (language) {
            case ru:
                dto.setName(tvSeries.getNameRu());
                break;
            case en:
                dto.setName(tvSeries.getNameEn());
                break;
            default:
                dto.setName(tvSeries.getNameEn());
                break;
        }
        dto.setFirstAirDate(tvSeries.getReleaseDate());

        return dto;
    }

}

