package com.barber.api.mapper;

import com.barber.api.dto.category.CategoryAdditionalInfoDto;
import com.barber.api.dto.category.CategoryAdminDto;
import com.barber.api.dto.category.CategoryDto;
import com.barber.api.form.category.CreateCategoryForm;
import com.barber.api.form.category.UpdateCategoryForm;
import com.barber.api.model.Category;
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
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "isSelected", target = "isSelected")
  @BeanMapping(ignoreByDefault = true)
  Category fromCreateCategoryFormToEntity(CreateCategoryForm createCategoryForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "kind", target = "kind")
  @Mapping(source = "orderInParent", target = "orderInParent")
  @Mapping(source = "isSelected", target = "isSelected")
  @Mapping(source = "additionalInfo", target = "additionalInfoList", qualifiedByName = "fromEntityToCategoryAdditionalInfoDto")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToCategoryAdminDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCategoryAdminDto")
  CategoryAdminDto fromEntityToCategoryAdminDto(Category category);

  @IterableMapping(elementTargetType = CategoryAdminDto.class, qualifiedByName = "fromEntityToCategoryAdminDto")
  List<CategoryAdminDto> fromEntityToCategoryAdminDtoList(List<Category> categories);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "image", target = "image")
  @Mapping(source = "isSelected", target = "isSelected")
  @Mapping(source = "additionalInfo", target = "additionalInfoList", qualifiedByName = "fromEntityToCategoryAdditionalInfoDto")
  @Mapping(source = "parent", target = "parent", qualifiedByName = "fromEntityToCategoryDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCategoryDto")
  CategoryDto fromEntityToCategoryDto(Category category);

  @IterableMapping(elementTargetType = CategoryDto.class, qualifiedByName = "fromEntityToCategoryDto")
  List<CategoryDto> fromEntityToCategoryDtoList(List<Category> categories);

  @Named("fromEntityToCategoryAdditionalInfoDto")
  default List<CategoryAdditionalInfoDto> fromEntityToCategoryAdditionalInfoDto(String json) {
    return JsonUtils.convertJsonStringToList(json, CategoryAdditionalInfoDto.class);
  }

  @Mapping(source = "name", target = "name")
  @Mapping(source = "description", target = "description")
  @Mapping(source = "isSelected", target = "isSelected")
  @BeanMapping(ignoreByDefault = true)
  void fromUpdateCategoryFormToEntity(UpdateCategoryForm updateCategoryForm, @MappingTarget Category category);
}
