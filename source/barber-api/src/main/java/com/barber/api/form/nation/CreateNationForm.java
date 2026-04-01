package com.barber.api.form.nation;

import com.barber.api.validation.NationKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@ApiModel
public class CreateNationForm {
  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name", required = true)
  private String name;

  @NationKind
  @ApiModelProperty(name = "kind", value = "Kind (1: province, 2: district, 3: ward)", required = true)
  private Integer kind;

  @ApiModelProperty(name = "parentId")
  private Long parentId;
}
