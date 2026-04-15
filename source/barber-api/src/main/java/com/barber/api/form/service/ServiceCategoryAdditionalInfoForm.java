package com.barber.api.form.service;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ServiceCategoryAdditionalInfoForm {
  @ApiModelProperty(name = "id")
  private Long id;

  @ApiModelProperty(name = "name")
  private String name;

  @ApiModelProperty(name = "image")
  private String image;
}
