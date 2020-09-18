package com.ness.movie_release_web.util.validators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.getField;

@Slf4j
public class NotBlankIfFieldIsStringEqualValidator implements ConstraintValidator<NotBlankIfFieldIsEqual, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        boolean result = true;
        for (Field field : o.getClass().getDeclaredFields()) {

            NotBlankIfFieldIsStringEqual annotation = field.getAnnotation(NotBlankIfFieldIsStringEqual.class);
            if (annotation != null) {
                String fieldToCheckName = annotation.field();
                String expectedValue = annotation.value();
                Field fieldToCheck = findField(o.getClass(), fieldToCheckName);

                if (fieldToCheck == null) {
                    log.error(format("Field %s at %s not found", fieldToCheckName, o));
                    throw new IllegalStateException(format("Field %s at %s not found", fieldToCheckName, o));
                }
                field.setAccessible(true);
                fieldToCheck.setAccessible(true);

                String fieldToCheckValue = String.valueOf(getField(fieldToCheck, o));
                if (Objects.equals(expectedValue, fieldToCheckValue) && isBlank(String.valueOf(getField(field, o)))) {
                    result = false;
                    context.buildConstraintViolationWithTemplate(annotation.message()).addPropertyNode(field.getName()).addConstraintViolation();
                }
            }
        }

        return result;
    }
}
