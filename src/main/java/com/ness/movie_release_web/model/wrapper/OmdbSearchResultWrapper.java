package com.ness.movie_release_web.model.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OmdbSearchResultWrapper implements Serializable {

    @JsonProperty(value = "Search")
    private List<OmdbWrapper> films = new ArrayList<>();

    @JsonProperty(value = "totalResults")
    private int totalResults;
}
