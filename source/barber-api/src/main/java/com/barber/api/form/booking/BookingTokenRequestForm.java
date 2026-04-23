package com.barber.api.form.booking;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ApiModel
public class BookingTokenRequestForm {
  @NotBlank(message = "token cannot be null")
  @ApiModelProperty(name = "token")
  private String token;
}
