package com.barber.api.dto.bookingService;

import com.barber.api.dto.booking.BookingAdminDto;
import lombok.Data;

@Data
public class BookingServiceAdminDto {
  private Long id;
  private Long serviceId;
  private Double price;
  private ServiceInfoDto serviceInfo;
  private BookingAdminDto booking;
}
