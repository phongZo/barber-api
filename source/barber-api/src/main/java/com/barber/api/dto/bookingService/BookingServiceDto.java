package com.barber.api.dto.bookingService;

import lombok.Data;

@Data
public class BookingServiceDto {
  private Long id;
  private Long serviceId;
  private Double price;
  private ServiceInfoDto serviceInfo;
}
