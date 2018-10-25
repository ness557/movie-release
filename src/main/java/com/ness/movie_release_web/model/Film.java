package com.ness.movie_release_web.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "film")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Film {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "tmdb_id")
    private Integer tmdbId;

    @Column(name = "subs_date")
    private LocalDateTime subscriptionDate;

    @ManyToOne
    @JoinColumn(name = "uzer_id")
    private User user;
}
