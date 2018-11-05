package com.ness.movie_release_web.model.wrapper.tmdb.movie.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GenreWrapper {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;

}
