package com.barber.api.dto.time;

import lombok.Data;

@Data
public class TimeSlot {
  private String utcTime;
  private Boolean available;
}
