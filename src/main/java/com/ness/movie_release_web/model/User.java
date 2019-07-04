package com.ness.movie_release_web.model;

import lombok.*;

import javax.persistence.*;

import com.ness.movie_release_web.model.dto.tmdb.Language;
import lombok.experimental.Accessors;

@Entity
@Table(name = "uzer")
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "role")
    private String role;

    @Column(name = "login", unique = true)
    private String login;

    @Column(name = "encr_pass")
    private String encPassword;

    @Column(name = "telegram_id")
    private String telegramId;

    @Column(name = "email")
    private String email;

    @Column(name = "is_telegram_notify")
    private boolean isTelegramNotify;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Enumerated
    @Column(name = "language")
    private Language language;
}
