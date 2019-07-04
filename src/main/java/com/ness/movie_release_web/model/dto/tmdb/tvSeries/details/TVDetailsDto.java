package com.ness.movie_release_web.model.dto.tmdb.tvSeries.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.TVSeries;
import com.ness.movie_release_web.model.dto.tmdb.GenreDto;
import com.ness.movie_release_web.model.dto.tmdb.Language;
import com.ness.movie_release_web.model.dto.tmdb.ProductionCompanyDto;
import com.ness.movie_release_web.model.dto.tmdb.VideosDto;
import com.ness.movie_release_web.model.dto.tmdb.credits.PeopleCreditsDto;
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
public class TVDetailsDto {

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("backdrop_path")
    private String backdropPath;

    @JsonProperty("episode_run_time")
    private List<Long> episodeRunTime = new ArrayList<>();

    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    @JsonProperty("first_air_date")
    private LocalDate firstAirDate;

    @JsonProperty("genres")
    private List<GenreDto> genres = new ArrayList<>();

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
    private EpisodeDto lastEpisodeToAir;

    @JsonProperty("name")
    private String name;

    @JsonProperty("next_episode_to_air")
    private EpisodeDto nextEpisodeToAir;

    @JsonProperty("networks")
    private List<ProductionCompanyDto> networks = new ArrayList<>();

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
    private List<ProductionCompanyDto> productionCompanies = new ArrayList<>();

    @JsonProperty("seasons")
    private List<SeasonDto> seasons = new ArrayList<>();

    @JsonProperty("status")
    private Status status;

    @JsonProperty("type")
    private String type;

    @JsonProperty("vote_average")
    private Float voteAverage;

    @JsonProperty("vote_count")
    private Long voteCount;

    @JsonProperty("videos")
    private VideosDto videos;

    @JsonProperty("credits")
    private PeopleCreditsDto credits = new PeopleCreditsDto();

    public static TVDetailsDto of(TVSeries tvSeries, Language language) {
        TVDetailsDto dto = new TVDetailsDto();
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

