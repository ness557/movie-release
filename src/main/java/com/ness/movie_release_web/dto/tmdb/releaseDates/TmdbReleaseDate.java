package com.ness.movie_release_web.dto.tmdb.releaseDates;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ness.movie_release_web.util.tmdb.TmdbReleaseDateDateDeserializer;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TmdbReleaseDate implements Comparable {

    @JsonProperty("release_date")
    @JsonDeserialize(using = TmdbReleaseDateDateDeserializer.class)
    private LocalDate releaseDate;

    @JsonProperty("type")
    private ReleaseType releaseType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TmdbReleaseDate that = (TmdbReleaseDate) o;
        return Objects.equals(releaseDate, that.releaseDate) &&
                releaseType.equals(that.releaseType);
    }

    @Override
    public int compareTo(Object o) {
        if (this == o) return 0;
        if (o == null || getClass() != o.getClass()) return 1;
        TmdbReleaseDate that = (TmdbReleaseDate) o;
        if (this.releaseDate.isBefore(that.releaseDate))
            return -1;
        else if (this.releaseDate.isAfter(that.releaseDate))
            return 1;
        else
            return this.releaseType.ordinal()  - that.releaseType.ordinal();
    }
}
