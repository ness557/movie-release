package com.ness.movie_release_web.util.validators;

import com.ness.movie_release_web.model.dto.PasswordDto;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Validator for {@link MatchPassword}
 * passwordDto.password is equals to passwordDto.matchPassword OR passwordDto == null.
 *
 * @author Roman_Bezruchko
 */
@Slf4j
public class MatchPasswordValidator implements ConstraintValidator<MatchPassword, PasswordDto> {

    @Override
    public boolean isValid(PasswordDto value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }

        return Objects.equals(value.getPassword(), value.getMatchPassword());
    }
}
