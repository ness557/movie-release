package com.ness.movie_release_web.dto;

import com.ness.movie_release_web.model.type.MessageDestinationType;
import com.ness.movie_release_web.util.validators.MatchPassword;
import lombok.*;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class UserDto {

    private Long id;

    private String role;

    @NotEmpty(message = "{lang.login_error_msg}")
    private String login;

    @MatchPassword(message = "{lang.passwords_not_match}")
    private PasswordDto password;

    @NotBlank(message = "{lang.telegram_error_msg}")
    @Pattern(regexp = "^$|@\\w*", message = "{lang.telegram_error_msg}")
    private String telegramId;

    @Email(message = "{lang.email_error_msg}")
    @NotBlank(message = "{lang.email_error_msg}")
    private String email;

    private MessageDestinationType messageDestinationType;

    private Long telegramChatId;

    private Language language;
}
