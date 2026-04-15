package com.barber.api.dto.service;

import com.barber.api.dto.ABasicAdminDto;
import com.barber.api.dto.category.CategoryAdminDto;
import java.util.List;
import lombok.Data;

@Data
public class ServiceAdminDto extends ABasicAdminDto {
  private String name;
  private String description;
  private String additionalInfo;
  private Integer duration;
  private Double price;
  private Float saleOff;
  private String image;
  private String video;
  private String tag;
  private Boolean allowDetail;
  private CategoryAdminDto category;
  private List<ServiceCategoryOptionDto> optionList;
  private List<ServiceServiceStepDto> serviceStepList;
}
