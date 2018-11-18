package com.ness.movie_release_web.model.wrapper.tmdb.credits;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.wrapper.tmdb.tvSeries.details.Gender;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CrewWrapper {


    @JsonProperty("id")
    private Integer id;

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
