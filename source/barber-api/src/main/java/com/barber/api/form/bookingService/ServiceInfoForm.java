package com.barber.api.form.bookingService;

import lombok.Data;

@Data
public class ServiceInfoForm {
  private String name;
  private Double price;
  private Double saleOff;
}
