package com.ness.movie_release_web.model;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserWrapper {

    private String role;

    private String login;

    private String encPassword;

    private String telegramId;

    private String email;

    private boolean isTelegramNotify;

    public static UserWrapper wrap(User user){
        return new UserWrapper(user.getRole(),
                user.getLogin(),
                user.getEncPassword(),
                user.getTelegramId(),
                user.getEmail(),
                user.isTelegramNotify());
    }
}
