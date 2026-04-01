package com.barber.api.dto.account;

import lombok.Data;

@Data
public class AccountUserDto {
  private String username;
  private String fullName;
  private String email;
  private String phone;
  private String avatarPath;
}
