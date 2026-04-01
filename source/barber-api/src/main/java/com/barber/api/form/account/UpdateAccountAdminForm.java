package com.barber.api.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class UpdateAccountAdminForm {
    @NotNull(message = "id cannot be null")
    @ApiModelProperty(name = "id", required = true)
    private Long id;
    @ApiModelProperty(name = "email")
    private String email;
    @ApiModelProperty(name = "phone")
    private String phone;
    @ApiModelProperty(name = "password")
    private String password;
    @NotEmpty(message = "fullName cannot be null")
    @ApiModelProperty(name = "fullName", required = true)
    private String fullName;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath ;
    @NotNull(message = "groupId cannot be null")
    @ApiModelProperty(name = "groupId", required = true)
    private Long groupId;
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
}
