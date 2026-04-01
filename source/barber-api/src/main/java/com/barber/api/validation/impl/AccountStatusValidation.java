
package com.barber.api.validation.impl;

import com.barber.api.constant.BarberConstant;
import com.barber.api.validation.AccountStatus;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class AccountStatusValidation implements ConstraintValidator<AccountStatus, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = List.of(
            BarberConstant.STATUS_ACTIVE,
            BarberConstant.STATUS_PENDING,
            BarberConstant.STATUS_LOCK,
            BarberConstant.STATUS_DELETE
    );

    @Override
    public void initialize(AccountStatus constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer kind, ConstraintValidatorContext constraintValidatorContext) {
        if (kind == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(kind);
    }
}
