package com.barber.api.dto.nation;

import lombok.Data;

@Data
public class NationDto {
  private Long id;
  private String name;
  private Integer kind;
  private NationDto parent;
}
