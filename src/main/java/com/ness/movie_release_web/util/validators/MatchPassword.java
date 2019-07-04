package com.ness.movie_release_web.util.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MatchPassword.
 *
 * @author Roman_Bezruchko
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchPasswordValidator.class)
@Documented
public @interface MatchPassword {
    String message() default "Fields must be equals.";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}
