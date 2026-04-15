package com.barber.api.controller;

import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.service.ServiceAdminDto;
import com.barber.api.dto.service.ServiceDto;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.service.CreateServiceForm;
import com.barber.api.form.service.ServiceServiceStepForm;
import com.barber.api.form.service.UpdateServiceForm;
import com.barber.api.mapper.ServiceMapper;
import com.barber.api.model.Category;
import com.barber.api.model.Service;
import com.barber.api.model.criteria.ServiceCriteria;
import com.barber.api.repository.CategoryRepository;
import com.barber.api.repository.ServiceRepository;
import com.barber.api.utils.JsonUtils;
import java.util.Comparator;
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
@RequestMapping("/v1/service")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ServiceController extends ABasicController{
  @Autowired
  ServiceRepository serviceRepository;

  @Autowired
  CategoryRepository categoryRepository;

  @Autowired
  ServiceMapper serviceMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SER_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateServiceForm createServiceForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Service service = serviceMapper.fromCreateServiceFormToEntity(createServiceForm);
    if (createServiceForm.getCategoryId() != null){
      Category category = categoryRepository.findById(createServiceForm.getCategoryId())
          .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
      service.setCategory(category);
    }
    if (createServiceForm.getOptionList() != null){
      service.setOptions(JsonUtils.convertJsonToString(createServiceForm.getOptionList()));
    }
    if (createServiceForm.getServiceStepList() != null){
      List<ServiceServiceStepForm> serviceStepForms = createServiceForm.getServiceStepList();
      serviceStepForms.sort(
          Comparator.comparing
              (ServiceServiceStepForm::getOrder,
                  Comparator.nullsLast(Integer::compareTo)));
      service.setServiceStep(JsonUtils.convertJsonToString(serviceStepForms));
    }
    if (createServiceForm.getParentId() != null){
      Service parent = serviceRepository.findById(createServiceForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Service parent not found", ErrorCode.SERVICE_ERROR_NOT_FOUND));
      service.setParent(parent);
    }
    serviceRepository.save(service);
    apiMessageDto.setMessage("Create service success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SER_L')")
  public ApiMessageDto<ResponseListDto<List<ServiceAdminDto>>> listByAdmin(ServiceCriteria serviceCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<ServiceAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ServiceAdminDto>> responseListDto = new ResponseListDto<>();
    Page<Service> services = serviceRepository.findAll(serviceCriteria.getSpecification(), pageable);
    List<ServiceAdminDto> serviceAdminDtos = serviceMapper.fromEntityToServiceAdminDtoList(services.getContent());
    responseListDto.setContent(serviceAdminDtos);
    responseListDto.setTotalElements(services.getTotalElements());
    responseListDto.setTotalPages(services.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list service success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<ServiceDto>>> listByClient(ServiceCriteria serviceCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<ServiceDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ServiceDto>> responseListDto = new ResponseListDto<>();
    Page<Service> services = serviceRepository.findAll(serviceCriteria.getSpecification(), pageable);
    List<ServiceDto> serviceDtos = serviceMapper.fromEntityToServiceDtoList(services.getContent());
    responseListDto.setContent(serviceDtos);
    responseListDto.setTotalElements(services.getTotalElements());
    responseListDto.setTotalPages(services.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list service success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SER_V')")
  public ApiMessageDto<ServiceAdminDto> getByAdmin(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ServiceAdminDto> apiMessageDto = new ApiMessageDto<>();
    Service service = serviceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Service not found"));
    ServiceAdminDto serviceAdminDto = serviceMapper.fromEntityToServiceAdminDto(service);
    apiMessageDto.setData(serviceAdminDto);
    apiMessageDto.setMessage("Get detail service success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ServiceDto> getByClient(@PathVariable("id") Long id){
    ApiMessageDto<ServiceDto> apiMessageDto = new ApiMessageDto<>();
    Service service = serviceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Service not found"));
    ServiceDto serviceDto = serviceMapper.fromEntityToServiceDto(service);
    apiMessageDto.setData(serviceDto);
    apiMessageDto.setMessage("Get detail service success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SER_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateServiceForm updateServiceForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Service service = serviceRepository.findById(updateServiceForm.getId())
        .orElseThrow(() -> new NotFoundException("Service not found", ErrorCode.SERVICE_ERROR_NOT_FOUND));
    serviceMapper.fromUpdateServiceFormToEntity(updateServiceForm, service);
    if (updateServiceForm.getOptionList() != null){
      service.setOptions(JsonUtils.convertJsonToString(updateServiceForm.getOptionList()));
    }
    if (updateServiceForm.getServiceStepList() != null){
      List<ServiceServiceStepForm> serviceStepForms = updateServiceForm.getServiceStepList();
      serviceStepForms.sort(
          Comparator.comparing
              (ServiceServiceStepForm::getOrder,
                  Comparator.nullsLast(Integer::compareTo)));
      service.setServiceStep(JsonUtils.convertJsonToString(serviceStepForms));
    }
    if (!Objects.equals(updateServiceForm.getCategoryId(), service.getCategory() != null ? service.getCategory().getId() : null)) {
      if (updateServiceForm.getCategoryId() != null) {
        Category category = categoryRepository.findById(updateServiceForm.getCategoryId())
            .orElseThrow(() -> new NotFoundException("Category not found", ErrorCode.CATEGORY_ERROR_NOT_FOUND));
        service.setCategory(category);
      } else {
        service.setCategory(null);
      }
    }
    if (!Objects.equals(updateServiceForm.getParentId(), service.getParent() != null ? service.getParent().getId() : null)) {
      if (updateServiceForm.getParentId() != null) {
        Service parent = serviceRepository.findById(updateServiceForm.getParentId())
            .orElseThrow(() -> new NotFoundException("Service parent not found", ErrorCode.SERVICE_ERROR_NOT_FOUND));
        service.setParent(parent);
      } else {
        service.setParent(null);
      }
    }
    serviceRepository.save(service);
    apiMessageDto.setMessage("Update service success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SER_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Service service = serviceRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Service not found", ErrorCode.SERVICE_ERROR_NOT_FOUND));
    serviceRepository.setNullByParentId(id);
    serviceRepository.delete(service);
    apiMessageDto.setMessage("Delete service success");
    return apiMessageDto;
  }
}
