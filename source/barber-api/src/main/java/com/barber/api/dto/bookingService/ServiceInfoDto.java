package com.barber.api.dto.bookingService;

import lombok.Data;

@Data
public class ServiceInfoDto {
  private String name;
  private Double price;
  private Double saleOff;
}
