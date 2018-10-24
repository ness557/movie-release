package com.ness.movie_release_web.model.wrapper.tmdbReleaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.TmdbLocalDateDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReleaseDate implements Comparable {

    @JsonProperty("release_date")
    @JsonDeserialize(using = TmdbLocalDateDeserializer.class)
    private LocalDate releaseDate;

    @JsonProperty("type")
    private ReleaseType releaseType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReleaseDate that = (ReleaseDate) o;
        return Objects.equals(releaseDate, that.releaseDate) &&
                releaseType.equals(that.releaseType);
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 1;
        ReleaseDate that = (ReleaseDate) o;
        if (this.releaseDate.isBefore(that.releaseDate))
            return -1;
        else if (this.releaseDate.isAfter(that.releaseDate))
            return 1;
        else
            return this.releaseType.ordinal()  - that.releaseType.ordinal();
    }
}
