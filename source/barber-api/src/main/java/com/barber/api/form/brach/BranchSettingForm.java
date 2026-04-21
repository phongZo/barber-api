package com.barber.api.form.brach;

import com.barber.api.validation.Hour;
import lombok.Data;

@Data
public class BranchSettingForm {
  @Hour
  private String openTime;

  @Hour
  private String closeTime;
}
