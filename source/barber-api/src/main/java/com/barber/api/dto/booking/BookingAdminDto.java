package com.barber.api.dto.booking;

import com.barber.api.dto.ABasicAdminDto;
import com.barber.api.dto.branch.BranchAdminDto;
import com.barber.api.dto.customer.CustomerDto;
import java.util.Date;
import lombok.Data;

@Data
public class BookingAdminDto extends ABasicAdminDto {
  private BookingCustomerInfoDto customerInfo;
  private Double discount;
  private Double totalPrice;
  private Date bookingDate;
  private CustomerDto customer;
  private BranchAdminDto branch;
}
