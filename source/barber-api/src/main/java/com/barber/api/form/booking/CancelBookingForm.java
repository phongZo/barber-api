package com.barber.api.form.booking;

import com.barber.api.validation.Email;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CancelBookingForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @Email(allowNull = true)
  @ApiModelProperty(name = "email")
  private String email;
}
