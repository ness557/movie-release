package com.ness.movie_release_web.model.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.model.Film;
import com.ness.movie_release_web.util.OmdbLocalDateDeserializer;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ToString
public class OmdbWrapper implements Serializable {

    @JsonProperty(value = "Title")
    private String title;

    @JsonProperty(value = "Year")
    private String year;

    @JsonProperty(value = "imdbID")
    private String imdbId;

    @JsonProperty(value = "Type")
    private String type;

    @JsonProperty(value = "Poster")
    private String posterUrl;

    @JsonProperty(value = "DVD")
    @JsonDeserialize(using = OmdbLocalDateDeserializer.class)
    private LocalDate dvdDate;


    public String getDvdString(){
        if (dvdDate != null)
            return dvdDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy").withLocale(Locale.US));
        return "01 January 1970";
    }
}
