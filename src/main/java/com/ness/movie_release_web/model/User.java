package com.ness.movie_release_web.model;

import com.ness.movie_release_web.dto.Language;
import com.ness.movie_release_web.model.type.MessageDestinationType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Table(name = "uzer")
@Data
@Accessors(chain = true)
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

    @Column(name = "message_dest_type")
    @Enumerated(EnumType.STRING)
    private MessageDestinationType messageDestinationType;

    @Column(name = "telegram_chat_id")
    private Long telegramChatId;

    @Enumerated
    @Column(name = "language")
    private Language language;
}
