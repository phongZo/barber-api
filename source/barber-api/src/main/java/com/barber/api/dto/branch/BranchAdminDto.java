package com.barber.api.dto.branch;

import com.barber.api.dto.ABasicAdminDto;
import com.barber.api.dto.nation.NationAdminDto;
import lombok.Data;

@Data
public class BranchAdminDto extends ABasicAdminDto {
  private String name;
  private String addressLine;
  private NationAdminDto ward;
  private NationAdminDto district;
  private NationAdminDto province;
  private String phone;
  private String setting;
}
