package com.barber.api.controller;

import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.serviceStepTemplate.ServiceStepTemplateAdminDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.serviceStepTemplate.CreateServiceStepTemplateForm;
import com.barber.api.form.serviceStepTemplate.UpdateServiceStepTemplateForm;
import com.barber.api.mapper.ServiceStepTemplateMapper;
import com.barber.api.model.ServiceStepTemplate;
import com.barber.api.model.criteria.ServiceStepTemplateCriteria;
import com.barber.api.repository.ServiceStepTemplateRepository;
import java.util.List;
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
@RequestMapping("/v1/service-step-template")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ServiceStepTemplateController extends ABasicController{
  @Autowired
  ServiceStepTemplateRepository serviceStepTemplateRepository;

  @Autowired
  ServiceStepTemplateMapper serviceStepTemplateMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SST_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateServiceStepTemplateForm createServiceStepTemplateForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existStep = serviceStepTemplateRepository.existsByName(createServiceStepTemplateForm.getName());
    if (existStep){
      throw new BadRequestException("Step name already exist", ErrorCode.SERVICE_STEP_ERROR_EXIST);
    }
    ServiceStepTemplate serviceStepTemplate = serviceStepTemplateMapper.fromCreateServiceStepTemplateFormToEntity(createServiceStepTemplateForm);
    serviceStepTemplateRepository.save(serviceStepTemplate);
    apiMessageDto.setMessage("Create service step success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SST_L')")
  public ApiMessageDto<ResponseListDto<List<ServiceStepTemplateAdminDto>>> list(
      ServiceStepTemplateCriteria serviceStepTemplateCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<ServiceStepTemplateAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<ServiceStepTemplateAdminDto>> responseListDto = new ResponseListDto<>();
    Page<ServiceStepTemplate> serviceStepTemplates = serviceStepTemplateRepository.findAll(serviceStepTemplateCriteria.getSpecification(), pageable);
    List<ServiceStepTemplateAdminDto> serviceStepTemplateAdminDtos = serviceStepTemplateMapper.fromEntityToServiceStepTemplateAdminDtoList(serviceStepTemplates.getContent());
    responseListDto.setContent(serviceStepTemplateAdminDtos);
    responseListDto.setTotalElements(serviceStepTemplates.getTotalElements());
    responseListDto.setTotalPages(serviceStepTemplates.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list service step success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SST_V')")
  public ApiMessageDto<ServiceStepTemplateAdminDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ServiceStepTemplateAdminDto> apiMessageDto = new ApiMessageDto<>();
    ServiceStepTemplate serviceStepTemplate = serviceStepTemplateRepository.findById(id)
        .orElseThrow(() ->  new NotFoundException("Service step not found", ErrorCode.SERVICE_STEP_ERROR_NOT_FOUND));
    ServiceStepTemplateAdminDto serviceStepTemplateAdminDto = serviceStepTemplateMapper.fromEntityToServiceStepTemplateAdminDto(serviceStepTemplate);
    apiMessageDto.setData(serviceStepTemplateAdminDto);
    apiMessageDto.setMessage("Get detail service step success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SST_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateServiceStepTemplateForm updateServiceStepTemplateForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    ServiceStepTemplate serviceStepTemplate = serviceStepTemplateRepository.findById(updateServiceStepTemplateForm.getId())
        .orElseThrow(() ->  new NotFoundException("Service step not found", ErrorCode.SERVICE_STEP_ERROR_NOT_FOUND));
    if (!updateServiceStepTemplateForm.getName().equals(serviceStepTemplate.getName())){
      Boolean existStep = serviceStepTemplateRepository.existsByName(updateServiceStepTemplateForm.getName());
      if (existStep){
        throw new BadRequestException("Step name already exist", ErrorCode.SERVICE_STEP_ERROR_EXIST);
      }
    }
    serviceStepTemplateMapper.fromUpdateServiceStepTemplateFormToEntity(updateServiceStepTemplateForm, serviceStepTemplate);
    serviceStepTemplateRepository.save(serviceStepTemplate);
    apiMessageDto.setMessage("Update service step success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('SST_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    ServiceStepTemplate serviceStepTemplate = serviceStepTemplateRepository.findById(id)
        .orElseThrow(() ->  new NotFoundException("Service step not found", ErrorCode.SERVICE_STEP_ERROR_NOT_FOUND));
    serviceStepTemplateRepository.delete(serviceStepTemplate);
    return apiMessageDto;
  }
}
