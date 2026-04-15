package com.barber.api.form.service;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import lombok.Data;

@Data
public class ServiceCategoryOptionForm {
  @ApiModelProperty(name = "id")
  private Long id;

  @ApiModelProperty(name = "name")
  private String name;

  @ApiModelProperty(name = "description")
  private String description;

  @Valid
  @ApiModelProperty(name = "additionalInfoList")
  private List<ServiceCategoryAdditionalInfoForm> additionalInfoList;
}
