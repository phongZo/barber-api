package com.barber.api.form.booking;

import com.barber.api.validation.Date;
import com.barber.api.validation.Hour;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class CreateBookingForm {
  @Valid
  private BookingCustomerInfoForm customerInfo;

  @NotNull(message = "branchId cannot be null")
  @ApiModelProperty(name = "branchId")
  private Long branchId;

  @ApiModelProperty(name = "day")
  @Date
  private String day;

  @ApiModelProperty(name = "time")
  @Hour
  private String time;

  @NotNull(message = "discount cannot be null")
  @ApiModelProperty(name = "discount")
  private Double discount;

  @NotNull(message = "serviceIds cannot be null")
  @ApiModelProperty(name = "serviceIds")
  private List<Long> serviceIds;
}
