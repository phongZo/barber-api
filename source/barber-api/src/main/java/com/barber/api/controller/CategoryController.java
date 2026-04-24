package com.barber.api.controller;

import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.category.CategoryAdminDto;
import com.barber.api.dto.category.CategoryDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.category.CreateCategoryForm;
import com.barber.api.form.category.UpdateCategoryForm;
import com.barber.api.form.category.UpdateCategoryOrderForm;
import com.barber.api.mapper.CategoryMapper;
import com.barber.api.model.Category;
import com.barber.api.model.criteria.CategoryCriteria;
import com.barber.api.repository.CategoryRepository;
import com.barber.api.repository.ServiceRepository;
import com.barber.api.utils.JsonUtils;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/category")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CategoryController extends ABasicController{
  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ServiceRepository serviceRepository;

  @Autowired
  CategoryMapper categoryMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateCategoryForm createCategoryForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

    if (createCategoryForm.getParentId() != null){
      Boolean existCategory = categoryRepository.existsByNameAndKindAndParentId(createCategoryForm.getName(), createCategoryForm.getKind(), createCategoryForm.getParentId());
      if (existCategory){
        throw new BadRequestException("Category name already exist with this kind and parent", ErrorCode.CATEGORY_ERROR_EXIST);
      }
    } else {
      Boolean existCategory = categoryRepository.existsByNameAndKind(createCategoryForm.getName(), createCategoryForm.getKind());
      if (existCategory){
        throw new BadRequestException("Category name already exist with this kind", ErrorCode.CATEGORY_ERROR_EXIST);
      }
    }

    Category category = categoryMapper.fromCreateCategoryFormToEntity(createCategoryForm);
    Integer maxOrder = categoryRepository.findMaxOrderInParent(createCategoryForm.getKind(), createCategoryForm.getParentId());
    category.setOrderInParent(maxOrder + 1);
    if (createCategoryForm.getAdditionalInfoList() != null){
      category.setAdditionalInfo(JsonUtils.convertJsonToString(createCategoryForm.getAdditionalInfoList()));
    }
    if (createCategoryForm.getParentId() != null){
      Category parent = categoryRepository.findById(createCategoryForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      category.setParent(parent);
    }
    categoryRepository.save(category);
    apiMessageDto.setMessage("Create category success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_L')")
  public ApiMessageDto<ResponseListDto<List<CategoryAdminDto>>> listByAdmin(CategoryCriteria categoryCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<CategoryAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CategoryAdminDto>> responseListDto = new ResponseListDto<>();
    Page<Category> categories = categoryRepository.findAll(categoryCriteria.getSpecification(), pageable);
    List<CategoryAdminDto> categoryAdminDtos = categoryMapper.fromEntityToCategoryAdminDtoList(categories.getContent());
    responseListDto.setContent(categoryAdminDtos);
    responseListDto.setTotalElements(categories.getTotalElements());
    responseListDto.setTotalPages(categories.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list category success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<CategoryDto>>> listByClient(CategoryCriteria categoryCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<CategoryDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CategoryDto>> responseListDto = new ResponseListDto<>();
    Page<Category> categories = categoryRepository.findAll(categoryCriteria.getSpecification(), pageable);
    List<CategoryDto> categoryDtos = categoryMapper.fromEntityToCategoryDtoList(categories.getContent());
    responseListDto.setContent(categoryDtos);
    responseListDto.setTotalElements(categories.getTotalElements());
    responseListDto.setTotalPages(categories.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list category success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_V')")
  public ApiMessageDto<CategoryAdminDto> getByAdmin(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<CategoryAdminDto> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

    CategoryAdminDto categoryAdminDto = categoryMapper.fromEntityToCategoryAdminDto(category);
    apiMessageDto.setData(categoryAdminDto);
    apiMessageDto.setMessage("Get detail success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<CategoryDto> getByClient(@PathVariable("id") Long id){
    ApiMessageDto<CategoryDto> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

    CategoryDto categoryDto = categoryMapper.fromEntityToCategoryDto(category);
    apiMessageDto.setData(categoryDto);
    apiMessageDto.setMessage("Get detail success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateCategoryForm updateCategoryForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(updateCategoryForm.getId())
        .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

    if (!updateCategoryForm.getName().equals(category.getName())){
      if (category.getParent() != null){
        Boolean existCategory = categoryRepository.existsByNameAndKindAndParentId(updateCategoryForm.getName(), updateCategoryForm.getKind(), category.getParent().getId());
        if (existCategory){
          throw new BadRequestException("Category name already exist with this kind and parent", ErrorCode.CATEGORY_ERROR_EXIST);
        }
      } else {
        Boolean existCategory = categoryRepository.existsByNameAndKind(updateCategoryForm.getName(), updateCategoryForm.getKind());
        if (existCategory){
          throw new BadRequestException("Category name already exist with this kind", ErrorCode.CATEGORY_ERROR_EXIST);
        }
      }
    }

    Long oldParentId = category.getParent() != null ? category.getParent().getId() : null;
    boolean isChangeKind = !updateCategoryForm.getKind().equals(category.getKind());
    boolean isChangeParent = (oldParentId == null && updateCategoryForm.getParentId() != null)
        || (oldParentId != null && !oldParentId.equals(updateCategoryForm.getParentId()));

    if (isChangeKind || isChangeParent){
      categoryRepository.decreaseOrderAfterRemove(category.getKind(), oldParentId, category.getOrderInParent());
      Integer maxOrder = categoryRepository.findMaxOrderInParent(updateCategoryForm.getKind(), updateCategoryForm.getParentId());
      category.setOrderInParent(maxOrder + 1);
    }

    categoryMapper.fromUpdateCategoryFormToEntity(updateCategoryForm, category);
    if (updateCategoryForm.getAdditionalInfoList() != null){
      category.setAdditionalInfo(JsonUtils.convertJsonToString(updateCategoryForm.getAdditionalInfoList()));
    }

    if (isChangeParent){
      if (updateCategoryForm.getParentId() != null){
        Category parent = categoryRepository.findById(updateCategoryForm.getParentId())
            .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
        category.setParent(parent);
      } else {
        category.setParent(null);
      }
    }
    categoryRepository.save(category);
    apiMessageDto.setMessage("Update category success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
    Long oldParentId = category.getParent().getId() != null ? category.getParent().getId() : null;
    serviceRepository.setNullByCategoryId(id);
    categoryRepository.setNullChildrenByParentId(id);
    categoryRepository.decreaseOrderAfterRemove(category.getKind(), oldParentId, category.getOrderInParent());
    categoryRepository.delete(category);
    apiMessageDto.setMessage("Delete category success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update-order", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CA_UO')")
  public ApiMessageDto<String> updateOrder(@RequestBody UpdateCategoryOrderForm updateCategoryOrderForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Category category = categoryRepository.findById(updateCategoryOrderForm.getId())
        .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));

    Long oldParentId = category.getParent() != null ? category.getParent().getId() : null;
    if (Objects.equals(oldParentId, updateCategoryOrderForm.getParentId()) && category.getKind().equals(updateCategoryOrderForm.getKind())){
      if (updateCategoryOrderForm.getOrderInParent() < category.getOrderInParent()){
        categoryRepository.increaseOrderRange(category.getKind(), oldParentId, updateCategoryOrderForm.getOrderInParent(), category.getOrderInParent() - 1);
      } else if (updateCategoryOrderForm.getOrderInParent() > category.getOrderInParent()) {
        categoryRepository.decreaseOrderRange(category.getKind(), oldParentId, category.getOrderInParent() + 1, updateCategoryOrderForm.getOrderInParent());
      }
      category.setOrderInParent(updateCategoryOrderForm.getOrderInParent());
    } else {
      categoryRepository.decreaseOrderAfterRemove(category.getKind(), oldParentId, category.getOrderInParent());
      Integer maxOrder = categoryRepository.findMaxOrderInParent(updateCategoryOrderForm.getKind(), updateCategoryOrderForm.getParentId());
      category.setOrderInParent(maxOrder + 1);
      category.setKind(updateCategoryOrderForm.getKind());
      if (updateCategoryOrderForm.getParentId() != null){
        Category parent = categoryRepository.findById(updateCategoryOrderForm.getParentId())
            .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
        category.setParent(parent);
      } else {
        category.setParent(null);
      }
    }
    categoryRepository.save(category);
    apiMessageDto.setMessage("Update category order success");
    return apiMessageDto;
  }
}
