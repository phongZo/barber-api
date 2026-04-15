package com.barber.api.dto.serviceStepTemplate;

import com.barber.api.dto.ABasicAdminDto;
import lombok.Data;

@Data
public class ServiceStepTemplateAdminDto extends ABasicAdminDto {
  private String name;
  private String image;
}
