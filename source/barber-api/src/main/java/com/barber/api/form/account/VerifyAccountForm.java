package com.barber.api.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class VerifyAccountForm {
  @NotEmpty(message = "Idhash cannot be null.")
  @ApiModelProperty(name = "idHash", required = true)
  private String idHash;

  @NotEmpty(message = "OTP cannot be null.")
  @ApiModelProperty(name = "otp", required = true)
  private String otp;
}
