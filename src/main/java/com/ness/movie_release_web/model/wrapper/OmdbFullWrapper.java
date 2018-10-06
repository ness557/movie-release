package com.ness.movie_release_web.model.wrapper;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.OmdbImdbRatingDeserializer;
import com.ness.movie_release_web.util.OmdbLocalDateDeserializer;
import com.ness.movie_release_web.util.OmdbMetascoreRatingDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OmdbFullWrapper {

    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Year")
    private String year;

    @JsonProperty(value = "Rated")
    private String rated;

    @JsonProperty(value = "Released")
    @JsonDeserialize(using = OmdbLocalDateDeserializer.class)
    private LocalDate released;

    @JsonProperty(value = "Runtime")
    private String runtime;

    @JsonProperty(value = "Genre")
    private String genres;

    @JsonProperty(value = "Director")
    private String director;

    @JsonProperty(value = "Writer")
    private String writers;

    @JsonProperty(value = "Actors")
    private String actors;

    @JsonProperty(value = "Plot")
    private String plot;

    @JsonProperty(value = "Language")
    private String language;

    @JsonProperty(value = "Country")
    private String country;

    @JsonProperty(value = "Poster")
    private String poster;

    @JsonProperty(value = "Metascore")
    @JsonDeserialize(using = OmdbMetascoreRatingDeserializer.class)
    private Integer metascore;

    @JsonProperty(value = "imdbRating")
    @JsonDeserialize(using = OmdbImdbRatingDeserializer.class)
    private Float imdbRating;

    @JsonProperty(value = "imdbID")
    private String imdbId;

    @JsonProperty(value = "Type")
    private String type;

    @JsonProperty(value = "DVD")
    @JsonDeserialize(using = OmdbLocalDateDeserializer.class)
    private LocalDate dvd;

    @JsonProperty(value = "Production")
    private String production;

    @JsonProperty(value = "Website")
    private String website;

    public String getReleasedString() {
        if (released != null)
            return released.format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.US));
        return "01 January 1970";
    }

    public String getDvdString() {
        if (dvd != null)
            return dvd.format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.US));
        return "01 January 1970";
    }

}
