package com.barber.api.form.serviceStepTemplate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class CreateServiceStepTemplateForm {
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;

  @NotEmpty(message = "image cannot be null")
  @ApiModelProperty(name = "image")
  private String image;
}
