package com.barber.api.mapper;

import com.barber.api.dto.branch.BranchAdminDto;
import com.barber.api.dto.branch.BranchDto;
import com.barber.api.form.brach.CreateBranchForm;
import com.barber.api.form.brach.UpdateBranchForm;
import com.barber.api.model.Branch;
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
    uses = {NationMapper.class})
public interface BranchMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "addressLine", target = "addressLine")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "setting", target = "setting")
  @BeanMapping(ignoreByDefault = true)
  Branch fromCreateBranchFormToEntity(CreateBranchForm createBranchForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "addressLine", target = "addressLine")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "setting", target = "setting")
  @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToNationAdminDto")
  @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToNationAdminDto")
  @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToNationAdminDto")
  @Mapping(source = "status", target = "status")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBranchAdminDto")
  BranchAdminDto fromEntityToBranchAdminDto(Branch branch);

  @IterableMapping(elementTargetType = BranchAdminDto.class, qualifiedByName = "fromEntityToBranchAdminDto")
  List<BranchAdminDto> fromEntityToBranchAdminDtoList(List<Branch> branches);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "addressLine", target = "addressLine")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "setting", target = "setting")
  @Mapping(source = "ward", target = "ward", qualifiedByName = "fromEntityToNationDto")
  @Mapping(source = "district", target = "district", qualifiedByName = "fromEntityToNationDto")
  @Mapping(source = "province", target = "province", qualifiedByName = "fromEntityToNationDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToBranchDto")
  BranchDto fromEntityToBranchDto(Branch branch);

  @IterableMapping(elementTargetType = BranchDto.class, qualifiedByName = "fromEntityToBranchDto")
  List<BranchDto> fromEntityToBranchDtoList(List<Branch> branches);

  @Mapping(source = "name", target = "name")
  @Mapping(source = "addressLine", target = "addressLine")
  @Mapping(source = "phone", target = "phone")
  @Mapping(source = "setting", target = "setting")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateBranchFormToEntity(UpdateBranchForm updateBranchForm, @MappingTarget Branch branch);
}
