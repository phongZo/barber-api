package com.barber.api.mapper;

import com.barber.api.dto.nation.NationAdminDto;
import com.barber.api.dto.nation.NationDto;
import com.barber.api.form.nation.CreateNationForm;
import com.barber.api.form.nation.UpdateNationForm;
import com.barber.api.model.Nation;
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
public interface NationMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @BeanMapping(ignoreByDefault = true)
  Nation fromCreateNationFormToEntity(CreateNationForm createNationForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToNationDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToNationDto")
  NationDto fromEntityToNationDto(Nation nation);

  @IterableMapping(elementTargetType = NationDto.class, qualifiedByName = "fromEntityToNationDto")
  List<NationDto> fromEntityToNationDtoList(List<Nation> nations);


  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToNationDto")
  @Mapping(source = "createdDate", target = "createdDate")
  @Mapping(source = "modifiedDate", target = "modifiedDate")
  @BeanMapping(ignoreByDefault = true)
  NationAdminDto fromEntityToNationAdminDto(Nation nation);

  @IterableMapping(elementTargetType = NationAdminDto.class, qualifiedByName = "fromEntityToNationAdminDto")
  List<NationAdminDto> fromEntityToNationAdminDtoList(List<Nation> nations);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "kind", target = "kind")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateNationFormToEntity(UpdateNationForm updateNationForm, @MappingTarget Nation nation);
}
