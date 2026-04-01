package com.barber.api.dto.nation;

import com.barber.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class NationAdminDto extends ABasicAdminDto {
  private String name;
  private Integer kind;
  private NationAdminDto parent;
}
