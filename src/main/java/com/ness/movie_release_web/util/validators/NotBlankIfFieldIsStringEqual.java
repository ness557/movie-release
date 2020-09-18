package com.ness.movie_release_web.util.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * MatchPassword.
 *
 * @author Roman_Bezruchko
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotBlankIfFieldIsStringEqual {
    String message() default "Field must not be empty.";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

    String field();

    String value();
}
