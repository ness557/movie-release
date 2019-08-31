package com.ness.movie_release_web.model;

import com.ness.movie_release_web.dto.tmdb.movie.details.Status;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movie")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "users")
public class Movie {

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
    @JoinTable(name = "movie_uzer",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "uzer_id"))
    private List<User> users;
}
