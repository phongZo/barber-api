package com.barber.api.form.account;

import com.barber.api.validation.Email;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class ResendAccountForm {
  @Email
  @ApiModelProperty(name = "email", required = true)
  private String email;
}
