package com.ness.movie_release_web.dto.tmdb.people;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.dto.tmdb.Gender;
import com.ness.movie_release_web.dto.tmdb.credits.TmdbMovieCreditsDto;
import com.ness.movie_release_web.dto.tmdb.credits.TmdbTVCreditsDto;
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
@ToString
@EqualsAndHashCode
public class TmdbPeopleDto {

    @JsonProperty("birthday")
    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    private LocalDate birthday;

    @JsonProperty("known_for_department")
    private String knownForDepartment;

    @JsonProperty("deathday")
    @JsonDeserialize(using = TmdbMovieReleaseDateDeserializer.class)
    private LocalDate deathday;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("also_known_as")
    private List<String> alsoKnownAs = new ArrayList<>();

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("biography")
    private String biography;

    @JsonProperty("popularity")
    private Float popularity;

    @JsonProperty("place_of_birth")
    private String placeOfBirth;

    @JsonProperty("profile_path")
    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    private String profilePath;

    @JsonProperty("movie_credits")
    private TmdbMovieCreditsDto credits = new TmdbMovieCreditsDto();

    @JsonProperty("tv_credits")
    private TmdbTVCreditsDto tvCredits = new TmdbTVCreditsDto();
}
