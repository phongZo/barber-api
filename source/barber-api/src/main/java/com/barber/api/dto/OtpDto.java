package com.barber.api.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OtpDto {
  @ApiModelProperty(name = "idHash")
  private String idHash;
}
