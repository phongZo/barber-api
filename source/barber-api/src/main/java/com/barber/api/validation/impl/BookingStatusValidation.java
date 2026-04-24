package com.barber.api.validation.impl;

import com.barber.api.constant.BarberConstant;
import com.barber.api.validation.BookingStatus;
import com.barber.api.validation.CategoryKind;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingStatusValidation implements ConstraintValidator<BookingStatus, Integer> {
  private boolean allowNull;

  @Override
  public void initialize(BookingStatus constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    allowNull = constraintAnnotation.allowNull();
  }

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
    return value == null ? allowNull : BarberConstant.BOOKING_STATUS.contains(value);
  }
}
