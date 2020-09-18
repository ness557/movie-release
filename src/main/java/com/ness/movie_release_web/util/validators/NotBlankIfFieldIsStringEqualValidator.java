package com.ness.movie_release_web.util.validators;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;

@Slf4j
public class NotBlankIfFieldIsStringEqualValidator implements ConstraintValidator<NotBlankIfFieldIsStringEqual, Object> {

    private String fieldName;
    private String value;

    @Override
    public void initialize(NotBlankIfFieldIsStringEqual constraintAnnotation) {
        fieldName = constraintAnnotation.field();
        value = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {


        Field field = findField(o.getClass(), fieldName);
        if (field == null) {
            log.error(String.format("Field %s at %s not found", fieldName, o));
            return false;
        }
        field.setAccessible(true);

        String fieldValue = String.valueOf(getField(field, o));

        return Objects.equals(value, fieldValue);
    }
}
