package com.ness.movie_release_web.model;

import com.ness.movie_release_web.model.wrapper.tmdb.movie.details.Status;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "film")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Film {

    @Id
    private Long id;

    @Column(name = "name_en")
    private String nameEn;

    @Column(name = "name_ru")
    private String nameRu;

    @Enumerated
    private Status status;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(name = "vote_average")
    private Float voteAverage;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "film_uzer",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "uzer_id"))
    private List<User> users;
}
