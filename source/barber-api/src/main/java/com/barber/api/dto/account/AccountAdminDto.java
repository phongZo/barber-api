package com.barber.api.dto.account;

import com.barber.api.dto.ABasicAdminDto;
import com.barber.api.dto.group.GroupDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AccountAdminDto extends ABasicAdminDto {
    @ApiModelProperty(name = "kind")
    private int kind;
    @ApiModelProperty(name = "username")
    private String username;
    @ApiModelProperty(name = "phone")
    private String phone;
    @ApiModelProperty(name = "email")
    private String email;
    @ApiModelProperty(name = "fullName")
    private String fullName;
    @ApiModelProperty(name = "group")
    private GroupDto group;
    @ApiModelProperty(name = "avatarPath")
    private String avatarPath;
    private Boolean isSuperAdmin;
}
