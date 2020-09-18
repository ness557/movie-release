package com.ness.movie_release_web.util.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankIfFieldIsStringEqualValidator.class)
public @interface NotBlankIfFieldIsEqual {
    String message() default "Field must not be empty.";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}
