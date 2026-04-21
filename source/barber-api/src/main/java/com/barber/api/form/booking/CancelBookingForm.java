package com.barber.api.form.booking;

import com.barber.api.validation.Phone;
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

  @Phone(allowNull = true)
  @ApiModelProperty(name = "phone")
  private String phone;
}
