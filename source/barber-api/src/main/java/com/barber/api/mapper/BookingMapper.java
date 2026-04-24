package com.barber.api.mapper;

import com.barber.api.dto.booking.BookingAdminDto;
import com.barber.api.dto.booking.BookingCustomerInfoDto;
import com.barber.api.dto.booking.BookingDto;
import com.barber.api.model.Booking;
import com.barber.api.utils.JsonUtils;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CustomerMapper.class, BranchMapper.class})
public interface BookingMapper {

  @Mapping(source = "id", target = "id")
  @Mapping(source = "customerInfo", target = "customerInfo", qualifiedByName = "fromEntityToBookingCustomerInfoDto")
  @Mapping(source = "discount", target = "discount")
  @Mapping(source = "totalPrice", target = "totalPrice")
  @Mapping(source = "bookingDate", target = "bookingDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "customer", target = "customer", qualifiedByName = "fromEntityToCustomerDto")
  @Mapping(source = "branch", target = "branch", qualifiedByName = "fromEntityToBranchAdminDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBookingAdminDto")
  BookingAdminDto fromEntityToBookingAdminDto(Booking booking);

  @IterableMapping(elementTargetType = BookingAdminDto.class, qualifiedByName = "fromEntityToBookingAdminDto")
  List<BookingAdminDto> fromEntityToBookingAdminDtoList(List<Booking> bookings);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "customerInfo", target = "customerInfo", qualifiedByName = "fromEntityToBookingCustomerInfoDto")
  @Mapping(source = "discount", target = "discount")
  @Mapping(source = "totalPrice", target = "totalPrice")
  @Mapping(source = "bookingDate", target = "bookingDate")
  @Mapping(source = "status", target = "status")
  @Mapping(source = "customer", target = "customer", qualifiedByName = "fromEntityToCustomerProfileDto")
  @Mapping(source = "branch", target = "branch", qualifiedByName = "fromEntityToBranchDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBookingDto")
  BookingDto fromEntityToBookingDto(Booking booking);

  @IterableMapping(elementTargetType = BookingDto.class, qualifiedByName = "fromEntityToBookingDto")
  List<BookingDto> fromEntityToBookingDtoList(List<Booking> bookings);

  @Named("fromEntityToBookingCustomerInfoDto")
  default BookingCustomerInfoDto fromEntityToBookingCustomerInfoDto(String json) {
    return JsonUtils.convertJsonStringToClass(json, BookingCustomerInfoDto.class);
  }
}
