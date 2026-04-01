package com.barber.api.form.brach;

import com.barber.api.validation.Phone;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateBranchForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;
  @NotEmpty(message = "addressLine cannot be null")
  @ApiModelProperty(name = "addressLine")
  private String addressLine;
  @NotNull(message = "wardId cannot be null")
  @ApiModelProperty(name = "wardId")
  private Long wardId;
  @NotNull(message = "districtId cannot be null")
  @ApiModelProperty(name = "districtId")
  private Long districtId;
  @NotNull(message = "provinceId cannot be null")
  @ApiModelProperty(name = "provinceId")
  private Long provinceId;
  @Phone
  @ApiModelProperty(name = "addressLine")
  private String phone;
  @NotEmpty(message = "setting cannot be null")
  @ApiModelProperty(name = "setting")
  private String setting;
}
