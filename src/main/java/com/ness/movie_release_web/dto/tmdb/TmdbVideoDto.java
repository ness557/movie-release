package com.ness.movie_release_web.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbVideoDeserializer;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TmdbVideoDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("key")
    @JsonDeserialize(using = TmdbVideoDeserializer.class)
    private String ytLink;

    @JsonProperty("name")
    private String name;

    @JsonProperty("site")
    private String site;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("type")
    private String type;
}
