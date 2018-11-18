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
public class CastWrapper {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("credit_id")
    private String creditId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private Gender gender;

    @JsonProperty("character")
    private String character;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("profile_path")
    private String profilePath;
}
