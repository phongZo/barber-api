package com.barber.api.form.category;

import com.barber.api.validation.CategoryKind;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateCategoryForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;

  @ApiModelProperty(name = "description")
  private String description;

  @ApiModelProperty(name = "description")
  private String image;

  @CategoryKind
  @ApiModelProperty(name = "kind")
  private Integer kind;

  @NotNull(message = "isSelected cannot be null")
  @ApiModelProperty(name = "isSelected")
  private Boolean isSelected;

  @Valid
  private List<CategoryAdditionalInfoForm> additionalInfoList;

  @ApiModelProperty(name = "parentId")
  private Long parentId;
}
