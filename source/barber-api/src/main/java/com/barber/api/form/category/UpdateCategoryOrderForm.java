package com.barber.api.form.category;

import com.barber.api.validation.CategoryKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateCategoryOrderForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @CategoryKind
  @ApiModelProperty(name = "kind")
  private Integer kind;

  @NotNull(message = "orderInParent cannot be null")
  @ApiModelProperty(name = "orderInParent")
  private Integer orderInParent;

  @ApiModelProperty(name = "parentId")
  private Long parentId;
}
