package com.barber.api.dto.category;

import lombok.Data;

@Data
public class CategoryAdditionalInfoDto {
  private Long id;
  private String name;
  private String image;
  private String additionalInfo;
}
