package com.barber.api.dto.branch;

import com.barber.api.dto.nation.NationDto;
import lombok.Data;

@Data
public class BranchDto {
  private Long id;
  private String name;
  private String addressLine;
  private NationDto ward;
  private NationDto district;
  private NationDto province;
  private String phone;
  private BranchSettingDto setting;
}
