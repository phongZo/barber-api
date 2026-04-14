package com.barber.api.dto.category;

import com.barber.api.dto.ABasicAdminDto;
import java.util.List;
import lombok.Data;

@Data
public class CategoryAdminDto extends ABasicAdminDto {
  private String name;
  private String description;
  private Integer kind;
  private String image;
  private Integer orderInParent;
  private Boolean isSelected;
  private List<CategoryAdditionalInfoDto> additionalInfoList;
  private CategoryAdminDto parent;
}
