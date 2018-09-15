package com.ness.movie_release_web.model;

import lombok.*;
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

    @NotEmpty
    @Column(name = "login")
    private String login;

    @NotEmpty
    @Column(name = "encr_pass")
    private String encPassword;

    @Transient
    private String matchPassword;

    @Pattern(regexp = "^$|@\\w*")
    @Column(name = "telegram_id")
    private String telegramId;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "is_telegram_notify")
    private boolean isTelegramNotify;

}
