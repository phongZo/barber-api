package com.barber.api.form.booking;

import com.barber.api.validation.Phone;
import lombok.Data;

@Data
public class BookingCustomerInfoForm {
  @Phone
  private String phone;
}
