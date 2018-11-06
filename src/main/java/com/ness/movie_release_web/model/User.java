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
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "role")
    private String role;

    @NotEmpty(message = "{lang.login_error_msg}")
    @Column(name = "login", unique = true)
    private String login;

    @NotEmpty(message = "{lang.password_error_msg}")
    @Column(name = "encr_pass")
    private String encPassword;

    @Transient
    private String matchPassword;

    @Pattern(regexp = "^$|@\\w*", message = "{lang.telegram_error_msg}")
    @Column(name = "telegram_id")
    private String telegramId;

    @Email(message = "{lang.email_error_msg}")
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
