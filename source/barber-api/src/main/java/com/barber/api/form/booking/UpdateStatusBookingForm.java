package com.barber.api.form.booking;

import com.barber.api.validation.BookingStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateStatusBookingForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @BookingStatus
  @ApiModelProperty(name = "status")
  private Integer status;
}
