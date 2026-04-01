package com.barber.api.dto.customer;

import com.barber.api.dto.account.AccountDto;
import java.util.Date;
import lombok.Data;

@Data
public class CustomerDto {
  private Long id;
  private Date birthday;
  private Long score;
  private AccountDto account;
}
