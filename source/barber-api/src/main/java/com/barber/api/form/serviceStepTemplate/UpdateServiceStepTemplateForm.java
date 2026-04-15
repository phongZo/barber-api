package com.barber.api.form.serviceStepTemplate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateServiceStepTemplateForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;

  @NotEmpty(message = "image cannot be null")
  @ApiModelProperty(name = "image")
  private String image;
}
