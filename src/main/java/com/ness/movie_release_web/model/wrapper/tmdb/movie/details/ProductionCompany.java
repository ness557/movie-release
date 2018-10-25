package com.ness.movie_release_web.model.wrapper.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ProductionCompany {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("logo_path")
    private Object logoPath;
    @JsonProperty("name")
    private String name;
    @JsonProperty("origin_country")
    private String originCountry;
}
