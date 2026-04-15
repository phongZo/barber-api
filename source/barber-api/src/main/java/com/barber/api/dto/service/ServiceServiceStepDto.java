package com.barber.api.dto.service;

import lombok.Data;

@Data
public class ServiceServiceStepDto {
  private Long id;
  private String name;
  private String image;
  private Integer order;
}
