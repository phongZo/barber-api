package com.barber.api.form.account;

import com.barber.api.validation.Password;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
@ApiModel
public class ForgetPasswordForm {
    @NotEmpty(message = "OPT can not be null.")
    @ApiModelProperty(name = "otp", required = true)
    private String otp;

    @NotEmpty(message = "Email can not be null.")
    @ApiModelProperty(name = "idHash", required = true)
    private String idHash;

    @Password
    @ApiModelProperty(name = "newPassword", required = true)
    private String newPassword;
}
