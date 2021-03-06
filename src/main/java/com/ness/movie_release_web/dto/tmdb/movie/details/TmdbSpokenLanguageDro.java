package com.ness.movie_release_web.dto.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TmdbSpokenLanguageDro {

    @JsonProperty("iso_639_1")
    private String iso6391;
    @JsonProperty("name")
    private String name;
}
