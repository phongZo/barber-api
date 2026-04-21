package com.barber.api.dto.booking;

import lombok.Data;

@Data
public class BookingCustomerInfoDto {
  private String phone;
  private String clientKey;
  private String bookingCode;
}
