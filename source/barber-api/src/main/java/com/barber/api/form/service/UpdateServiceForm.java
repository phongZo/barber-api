package com.barber.api.form.service;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@ApiModel
public class UpdateServiceForm {
  @NotNull(message = "id cannot be null")
  @ApiModelProperty(name = "id")
  private Long id;

  @NotEmpty(message = "name cannot be null")
  @ApiModelProperty(name = "name")
  private String name;

  @ApiModelProperty(name = "description")
  private String description;

  @ApiModelProperty(name = "additionalInfo")
  private String additionalInfo;

  @ApiModelProperty(name = "duration")
  private Integer duration;

  @ApiModelProperty(name = "price")
  private Double price;

  @ApiModelProperty(name = "saleOff")
  private Float saleOff;

  @ApiModelProperty(name = "image")
  private String image;

  @ApiModelProperty(name = "video")
  private String video;

  @ApiModelProperty(name = "tag")
  private String tag;

  @ApiModelProperty(name = "allowDetail")
  private Boolean allowDetail;

  @ApiModelProperty(name = "categoryId")
  private Long categoryId;

  @ApiModelProperty(name = "parentId")
  private Long parentId;

  @Valid
  @ApiModelProperty("optionList")
  private List<ServiceCategoryOptionForm> optionList;

  @Valid
  @ApiModelProperty(name = "serviceStepList")
  private List<ServiceServiceStepForm> serviceStepList;
}
