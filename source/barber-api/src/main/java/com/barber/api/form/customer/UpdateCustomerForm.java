package com.barber.api.form.customer;

import com.barber.api.validation.Email;
import com.barber.api.validation.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import lombok.Data;

@Data
@ApiModel
public class UpdateCustomerForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "username cannot be null")
  @ApiModelProperty(name = "username", required = true)
  private String username;
  @Email
  @ApiModelProperty(name = "email", required = true)
  private String email;
  @Phone
  @ApiModelProperty(name = "phone", required = true)
  private String phone;
  @ApiModelProperty(name = "password")
  private String password;
  @NotEmpty(message = "fullName cannot be null")
  @ApiModelProperty(name = "fullName",example = "Trung Hao")
  private String fullName;
  @ApiModelProperty(name = "birthday")
  @Past(message = "birthday must be in the past")
  private Date birthday;
  @ApiModelProperty(name = "avatarPath")
  private String avatarPath;
  @ApiModelProperty(name = "status")
  private Integer status;
}
