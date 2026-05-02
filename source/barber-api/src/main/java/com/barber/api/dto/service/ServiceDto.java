package com.barber.api.dto.service;

import com.barber.api.dto.category.CategoryDto;
import java.util.List;
import lombok.Data;

@Data
public class ServiceDto{
  private Long id;
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
  private CategoryDto category;
  private List<ServiceCategoryOptionDto> optionList;
  private List<ServiceServiceStepDto> serviceStepList;
  private ServiceDto parent;
}
