package com.barber.api.form.service;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceServiceStepForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;

  @NotEmpty(message = "image cannot be null")
  @ApiModelProperty(name = "image")
  private String image;

  @NotNull(message = "order cannot be null")
  @ApiModelProperty(name = "order")
  private Integer order;
}
