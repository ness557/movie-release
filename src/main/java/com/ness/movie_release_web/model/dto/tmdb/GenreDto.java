package com.ness.movie_release_web.model.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class GenreDto {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;

}
