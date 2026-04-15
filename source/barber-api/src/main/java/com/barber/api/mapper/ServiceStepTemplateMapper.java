package com.barber.api.mapper;

import com.barber.api.dto.serviceStepTemplate.ServiceStepTemplateAdminDto;
import com.barber.api.form.serviceStepTemplate.CreateServiceStepTemplateForm;
import com.barber.api.form.serviceStepTemplate.UpdateServiceStepTemplateForm;
import com.barber.api.model.ServiceStepTemplate;
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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceStepTemplateMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "image", target = "image")
  @BeanMapping(ignoreByDefault = true)
  ServiceStepTemplate fromCreateServiceStepTemplateFormToEntity(CreateServiceStepTemplateForm createServiceStepTemplateForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToServiceStepTemplateAdminDto")
  ServiceStepTemplateAdminDto fromEntityToServiceStepTemplateAdminDto(ServiceStepTemplate serviceStepTemplate);

  @IterableMapping(elementTargetType = ServiceStepTemplateAdminDto.class, qualifiedByName = "fromEntityToServiceStepTemplateAdminDto")
  List<ServiceStepTemplateAdminDto> fromEntityToServiceStepTemplateAdminDtoList(List<ServiceStepTemplate> serviceStepTemplates);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "image", target = "image")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateServiceStepTemplateFormToEntity(UpdateServiceStepTemplateForm updateServiceStepTemplateForm, @MappingTarget ServiceStepTemplate serviceStepTemplate);
}
