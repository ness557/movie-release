package com.ness.movie_release_web.dto.tmdb.credits;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.dto.tmdb.Gender;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "profilePath")
@EqualsAndHashCode
public class TmdbPeopleCrewDto {


    @JsonProperty("id")
    private Long id;

    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("department")
    private String department;

    @JsonProperty("job")
    private String job;

    @JsonProperty("gender")
    private Gender gender;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("profile_path")
    private String profilePath;
}
