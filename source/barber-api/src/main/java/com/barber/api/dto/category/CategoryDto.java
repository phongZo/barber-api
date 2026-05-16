package com.barber.api.dto.category;

import java.util.List;
import lombok.Data;

@Data
public class CategoryDto{
  private Long id;
  private String name;
  private String description;
  private String image;
  private Boolean isSelected;
  private Integer kind;
  private Integer orderInParent;
  private List<CategoryAdditionalInfoDto> additionalInfoList;
  private CategoryDto parent;
}
