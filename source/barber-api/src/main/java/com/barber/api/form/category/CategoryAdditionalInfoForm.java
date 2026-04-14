package com.barber.api.form.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class CategoryAdditionalInfoForm {
  @ApiModelProperty(name = "id")
  private Long id;

  @ApiModelProperty(name = "name")
  private String name;

  @ApiModelProperty(name = "image")
  private String image;

  @ApiModelProperty(name = "additionalInfo")
  private String additionalInfo;
}
