package com.barber.api.validation.impl;

import com.barber.api.validation.Password;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class PasswordValidation implements ConstraintValidator<Password, String> {
    private boolean allowNull;
    private String pattern;

    @Override
    public void initialize(Password constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
        pattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isBlank(value) ? allowNull : value.matches(pattern);
    }
}
