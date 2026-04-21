package com.barber.api.dto.booking;

import com.barber.api.dto.branch.BranchDto;
import com.barber.api.dto.customer.CustomerProfileDto;
import java.util.Date;
import lombok.Data;

@Data
public class BookingDto {
  private Long id;
  private BookingCustomerInfoDto customerInfo;
  private Double discount;
  private Double totalPrice;
  private Date bookingDate;
  private CustomerProfileDto customer;
  private BranchDto branch;
  private Integer status;
}
