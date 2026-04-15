package com.barber.api.mapper;

import com.barber.api.dto.service.ServiceAdminDto;
import com.barber.api.dto.service.ServiceCategoryOptionDto;
import com.barber.api.dto.service.ServiceDto;
import com.barber.api.dto.service.ServiceServiceStepDto;
import com.barber.api.form.service.CreateServiceForm;
import com.barber.api.form.service.UpdateServiceForm;
import com.barber.api.model.Service;
import com.barber.api.utils.JsonUtils;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {CategoryMapper.class})
public interface ServiceMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "additionalInfo", target = "additionalInfo")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "saleOff", target = "saleOff")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "video", target = "video")
  @Mapping(source = "tag", target = "tag")
  @Mapping(source = "allowDetail", target = "allowDetail")
  @BeanMapping(ignoreByDefault = true)
  Service fromCreateServiceFormToEntity(CreateServiceForm createServiceForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "additionalInfo", target = "additionalInfo")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "saleOff", target = "saleOff")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "video", target = "video")
  @Mapping(source = "tag", target = "tag")
  @Mapping(source = "allowDetail", target = "allowDetail")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryAdminDto")
  @Mapping(source = "options", target = "optionList", qualifiedByName = "fromJsonToServiceCategoryOptionDto")
  @Mapping(source = "serviceStep", target = "serviceStepList", qualifiedByName = "fromJsonToServiceStepDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToServiceAdminDto")
  ServiceAdminDto fromEntityToServiceAdminDto(Service service);

  @IterableMapping(elementTargetType = ServiceAdminDto.class, qualifiedByName = "fromEntityToServiceAdminDto")
  List<ServiceAdminDto> fromEntityToServiceAdminDtoList(List<Service> services);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "additionalInfo", target = "additionalInfo")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "saleOff", target = "saleOff")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "video", target = "video")
  @Mapping(source = "tag", target = "tag")
  @Mapping(source = "allowDetail", target = "allowDetail")
  @Mapping(source = "category", target = "category", qualifiedByName = "fromEntityToCategoryAdminDto")
  @Mapping(source = "options", target = "optionList", qualifiedByName = "fromJsonToServiceCategoryOptionDto")
  @Mapping(source = "serviceStep", target = "serviceStepList", qualifiedByName = "fromJsonToServiceStepDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToServiceDto")
  ServiceDto fromEntityToServiceDto(Service service);

  @IterableMapping(elementTargetType = ServiceDto.class, qualifiedByName = "fromEntityToServiceDto")
  List<ServiceDto> fromEntityToServiceDtoList(List<Service> services);

  @Named("fromJsonToServiceCategoryOptionDto")
  default List<ServiceCategoryOptionDto> fromJsonToServiceCategoryOptionDto(String json) {
    return JsonUtils.convertJsonStringToList(json, ServiceCategoryOptionDto.class);
  }

  @Named("fromJsonToServiceStepDto")
  default List<ServiceServiceStepDto> fromJsonToServiceStepDto(String json) {
    return JsonUtils.convertJsonStringToList(json, ServiceServiceStepDto.class);
  }

  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "additionalInfo", target = "additionalInfo")
  @Mapping(source = "duration", target = "duration")
  @Mapping(source = "price", target = "price")
  @Mapping(source = "saleOff", target = "saleOff")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "video", target = "video")
  @Mapping(source = "tag", target = "tag")
  @Mapping(source = "allowDetail", target = "allowDetail")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateServiceFormToEntity(UpdateServiceForm updateServiceForm, @MappingTarget Service service);
}
