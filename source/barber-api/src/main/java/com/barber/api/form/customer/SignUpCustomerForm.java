package com.barber.api.form.customer;

import com.barber.api.validation.Email;
import com.barber.api.validation.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Past;
import lombok.Data;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@ApiModel
public class SignUpCustomerForm {
    @NotEmpty(message = "username cannot be null")
    @ApiModelProperty(name = "username", required = true)
    private String username;
    @Email
    @ApiModelProperty(name = "email", required = true)
    private String email;
    @Phone
    @ApiModelProperty(name = "phone", required = true)
    private String phone;
    @NotEmpty(message = "password cannot be null")
    @ApiModelProperty(name = "password", required = true)
    private String password;
    @NotEmpty(message = "fullName cannot be null")
    @ApiModelProperty(name = "fullName",example = "Trung Hao")
    private String fullName;
    @ApiModelProperty(name = "birthday")
    @Past(message = "birthday must be in the past")
    private Date birthday;
}
