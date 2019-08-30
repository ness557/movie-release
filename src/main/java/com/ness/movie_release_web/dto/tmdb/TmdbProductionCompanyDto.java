package com.ness.movie_release_web.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbPosterPathDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TmdbProductionCompanyDto {

    @JsonProperty("id")
    private Long id;

    @JsonDeserialize(using = TmdbPosterPathDeserializer.class)
    @JsonProperty("logo_path")
    private String logoPath;

    @JsonProperty("name")
    private String name;

    @JsonProperty("origin_country")
    private String originCountry;
}
