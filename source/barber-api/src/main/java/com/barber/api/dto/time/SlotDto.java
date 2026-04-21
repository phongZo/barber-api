package com.barber.api.dto.time;

import java.util.List;
import lombok.Data;

@Data
public class SlotDto {
  private String label;
  private String dayType;
  private List<TimeSlot> timeSlots;
}
