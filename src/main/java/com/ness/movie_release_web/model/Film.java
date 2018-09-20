package com.ness.movie_release_web.model;

import lombok.*;
import javax.persistence.*;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "film")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Film {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "imdb_id")
    private String imdbId;

    @Column(name = "title")
    private String title;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "type")
    private String type;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "dvd_date")
    private LocalDate dvdDate;

    @Column(name = "subs_date")
    private LocalDate subscriptionDate;

    @ManyToOne
    @JoinColumn(name = "uzer_id")
    private User user;
}
