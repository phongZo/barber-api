package com.barber.api.form.booking;

import com.barber.api.validation.Email;
import lombok.Data;

@Data
public class BookingCustomerInfoForm {
  @Email
  private String email;
}
