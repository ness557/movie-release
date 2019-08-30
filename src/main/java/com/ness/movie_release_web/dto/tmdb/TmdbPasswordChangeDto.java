package com.ness.movie_release_web.dto.tmdb;

import com.ness.movie_release_web.dto.PasswordDto;
import com.ness.movie_release_web.util.validators.MatchPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * PasswordChangeDto.
 *
 * @author Roman_Bezruchko
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class TmdbPasswordChangeDto {

    private String oldPassword;

    @MatchPassword(message = "{lang.passwords_not_match}")
    private PasswordDto password;
}
