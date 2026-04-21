package com.barber.api.service;

import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.branch.BranchSettingDto;
import com.barber.api.dto.time.SlotDto;
import com.barber.api.dto.time.TimeSlot;
import com.barber.api.model.Branch;
import com.barber.api.utils.JsonUtils;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {
  public List<SlotDto> getAvailableSlot(Branch branch){
    BranchSettingDto setting =
        JsonUtils.convertJsonStringToClass(branch.getSetting(), BranchSettingDto.class);

    LocalTime openTime = LocalTime.parse(setting.getOpenTime());
    LocalTime closeTime = LocalTime.parse(setting.getCloseTime());

    List<SlotDto> responses = new ArrayList<>();
    LocalDate todayInVN = LocalDate.now(BarberConstant.VN_ZONE);

    for (int i = 0; i < 7; i++) {
      LocalDate targetDate = todayInVN.plusDays(i);
      responses.add(generateSlotsForDate(targetDate, i, openTime, closeTime));
    }

    return responses;
  }

  private SlotDto generateSlotsForDate(LocalDate date, int dayIndex, LocalTime open, LocalTime close) {
    String label = createDisplayLabel(date, dayIndex);
    String dayType = (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) ? "Cuối tuần" : "Ngày thường";

    List<TimeSlot> timeSlots = new ArrayList<>();

    Instant nowUtc = Instant.now();

    while (!open.isAfter(close.minusMinutes(BarberConstant.SLOT_INTERVAL))) {
      ZonedDateTime vnTime = ZonedDateTime.of(date, open, BarberConstant.VN_ZONE);
      Instant utcInstant = vnTime.toInstant();

      boolean available = !utcInstant.isBefore(nowUtc);

      TimeSlot slot = new TimeSlot();
      slot.setUtcTime(utcInstant.toString());
      slot.setAvailable(available);

      timeSlots.add(slot);

      open = open.plusMinutes(BarberConstant.SLOT_INTERVAL);
    }

    SlotDto slotDto = new SlotDto();
    slotDto.setLabel(label);
    slotDto.setDayType(dayType);
    slotDto.setTimeSlots(timeSlots);

    return slotDto;
  }

  private String createDisplayLabel(LocalDate date, int index) {
    String prefix;

    switch (index) {
      case 0:
        prefix = "Hôm nay";
        break;
      case 1:
        prefix = "Ngày mai";
        break;
      case 2:
        prefix = "Ngày kia";
        break;
      default:
        prefix = null;
    }

    if (prefix != null){
      return String.format("%s, %s (%s)",
          prefix,
          getShortDayName(date),
          date.format(DateTimeFormatter.ofPattern("dd/MM")));
    } else {
      return String.format("%s (%s)",
          getShortDayName(date),
          date.format(DateTimeFormatter.ofPattern("dd/MM")));
    }
  }

  private String getShortDayName(LocalDate d) {
    switch (d.getDayOfWeek()) {
      case MONDAY:
        return "T2";
      case TUESDAY:
        return "T3";
      case WEDNESDAY:
        return "T4";
      case THURSDAY:
        return "T5";
      case FRIDAY:
        return "T6";
      case SATURDAY:
        return "T7";
      case SUNDAY:
        return "CN";
      default:
        return "";
    }
  }
}
