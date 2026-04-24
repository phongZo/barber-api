package com.barber.api.dto.bookingService;

import com.barber.api.dto.booking.BookingDto;
import lombok.Data;

@Data
public class BookingServiceDto {
  private Long id;
  private Long serviceId;
  private Double price;
  private ServiceInfoDto serviceInfo;
  private BookingDto booking;
}
