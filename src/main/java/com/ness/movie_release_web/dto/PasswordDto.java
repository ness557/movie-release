package com.ness.movie_release_web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;

/**
 * PasswordDto.
 *
 * @author Roman_Bezruchko
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class PasswordDto {

    @NotEmpty(message = "{lang.password_error_msg}")
    private String password;

    private String matchPassword;
}
