package com.barber.api.mapper;

import com.barber.api.dto.bookingService.BookingServiceAdminDto;
import com.barber.api.dto.bookingService.ServiceInfoDto;
import com.barber.api.model.BookingService;
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
    uses = {BookingMapper.class})
public interface BookingServiceMapper {
  @Mapping(source = "id", target = "id")
  @Mapping(source = "serviceId", target = "serviceId")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "serviceInfo", target = "serviceInfo", qualifiedByName = "fromEntityToServiceInfoDto")
  @Mapping(source = "booking", target = "booking", qualifiedByName = "fromEntityToBookingAdminDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBookingServiceAdminDto")
  BookingServiceAdminDto fromEntityToBookingServiceAdminDto(BookingService bookingService);

  @IterableMapping(elementTargetType = BookingServiceAdminDto.class, qualifiedByName = "fromEntityToBookingServiceAdminDto")
  List<BookingServiceAdminDto> fromEntityToBookingServiceAdminDtoList(List<BookingService> bookingServices);

  @Named("fromEntityToServiceInfoDto")
  default ServiceInfoDto fromEntityToServiceInfoDto(String json) {
    return JsonUtils.convertJsonStringToClass(json, ServiceInfoDto.class);
  }
}
