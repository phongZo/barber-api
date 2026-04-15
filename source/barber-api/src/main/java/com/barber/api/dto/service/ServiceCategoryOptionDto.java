package com.barber.api.dto.service;

import java.util.List;
import lombok.Data;

@Data
public class ServiceCategoryOptionDto {
  private Long id;
  private String name;
  private String description;
  private List<ServiceCategoryAdditionalInfoDto> additionalInfoList;
}
