package com.ness.movie_release_web.model;

import com.ness.movie_release_web.model.wrapper.tmdb.Language;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "uzer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "role")
    private String role;

    @NotEmpty(message = "can not be empty")
    @Column(name = "login")
    private String login;

    @NotEmpty(message = "can not be empty")
    @Column(name = "encr_pass")
    private String encPassword;

    @Transient
    private String matchPassword;

    @Pattern(regexp = "^$|@\\w*", message = "should starts with @")
    @Column(name = "telegram_id")
    private String telegramId;

    @Email(message = "Email has wrong format")
    @Column(name = "email")
    private String email;

    @Column(name = "is_telegram_notify")
    private boolean isTelegramNotify;

    @Column(name = "telegram_chat_id")
    @Nullable
    private Long telegramChatId;

    @Enumerated
    @Column(name = "language")
    private Language language;
}
