package com.barber.api.form.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel
public class CreateAccountAdminForm {
    @NotEmpty(message = "username cannot be null")
    @ApiModelProperty(name = "username", required = true)
    private String username;
    @ApiModelProperty(name = "email")
    @Email
    private String email;
    @ApiModelProperty(name = "phone")
    private String phone;
    @NotEmpty(message = "password cannot be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;
    @NotNull(message = "kind cannot be null")
    @ApiModelProperty(name = "kind", required = true)
    private Integer kind;
    @NotEmpty(message = "fullName cannot be null")
    @ApiModelProperty(name = "fullName",example = "Trung Hao",required = true)
    private String fullName;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
    @NotNull(message = "status cannot be null")
    @ApiModelProperty(name = "status", required = true)
    private Integer status;
    @NotNull(message = "groupId cannot be null")
    @ApiModelProperty(name = "groupId", required = true)
    private Long groupId;

}
