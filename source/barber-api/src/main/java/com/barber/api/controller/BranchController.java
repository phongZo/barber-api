package com.barber.api.controller;

import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.branch.BranchAdminDto;
import com.barber.api.dto.branch.BranchDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.brach.CreateBranchForm;
import com.barber.api.form.brach.UpdateBranchForm;
import com.barber.api.mapper.BranchMapper;
import com.barber.api.model.Branch;
import com.barber.api.model.Nation;
import com.barber.api.model.criteria.BranchCriteria;
import com.barber.api.repository.BookingRepository;
import com.barber.api.repository.BranchRepository;
import com.barber.api.repository.NationRepository;
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
@RequestMapping("/v1/branch")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class BranchController extends ABasicController{
  @Autowired
  BranchRepository branchRepository;

  @Autowired
  NationRepository nationRepository;

  @Autowired
  BookingRepository bookingRepository;

  @Autowired
  BranchMapper branchMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BR_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateBranchForm createBranchForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Boolean existBranch = branchRepository.existsByName(createBranchForm.getName());
    if (existBranch){
      throw new BadRequestException("Branch name already exist", ErrorCode.BRANCH_ERROR_EXIST);
    }
    Nation[] address = getValidatedAddress(
        createBranchForm.getProvinceId(),
        createBranchForm.getDistrictId(),
        createBranchForm.getWardId()
    );

    Branch branch = branchMapper.fromCreateBranchFormToEntity(createBranchForm);
    branch.setWard(address[2]);
    branch.setDistrict(address[1]);
    branch.setProvince(address[0]);
    branchRepository.save(branch);

    apiMessageDto.setMessage("Create branch success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BR_L')")
  public ApiMessageDto<ResponseListDto<List<BranchAdminDto>>> listByAdmin(BranchCriteria branchCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<BranchAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BranchAdminDto>> responseListDto = new ResponseListDto<>();
    Page<Branch> branches = branchRepository.findAll(branchCriteria.getSpecification(), pageable);
    List<BranchAdminDto> branchAdminDtos = branchMapper.fromEntityToBranchAdminDtoList(branches.getContent());
    responseListDto.setContent(branchAdminDtos);
    responseListDto.setTotalElements(branches.getTotalElements());
    responseListDto.setTotalPages(branches.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list branch success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<BranchDto>>> listByClient(BranchCriteria branchCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<BranchDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BranchDto>> responseListDto = new ResponseListDto<>();
    Page<Branch> branches = branchRepository.findAll(branchCriteria.getSpecification(), pageable);
    List<BranchDto> branchDtos = branchMapper.fromEntityToBranchDtoList(branches.getContent());
    responseListDto.setContent(branchDtos);
    responseListDto.setTotalElements(branches.getTotalElements());
    responseListDto.setTotalPages(branches.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list branch success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BR_V')")
  public ApiMessageDto<BranchAdminDto> getByAdmin(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<BranchAdminDto> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));
    BranchAdminDto branchAdminDto = branchMapper.fromEntityToBranchAdminDto(branch);
    apiMessageDto.setData(branchAdminDto);
    apiMessageDto.setMessage("Get detail branch success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<BranchDto> getByClient(@PathVariable("id") Long id){
    ApiMessageDto<BranchDto> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));
    BranchDto branchDto = branchMapper.fromEntityToBranchDto(branch);
    apiMessageDto.setData(branchDto);
    apiMessageDto.setMessage("Get detail branch success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BR_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateBranchForm updateBranchForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(updateBranchForm.getId())
            .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));
    if(!updateBranchForm.getName().equals(branch.getName())){
      Boolean existBranch = branchRepository.existsByName(updateBranchForm.getName());
      if (existBranch){
        throw new BadRequestException("Branch name already exist", ErrorCode.BRANCH_ERROR_EXIST);
      }
    }

    if ((branch.getProvince() == null || branch.getDistrict() == null || branch.getWard() == null)
        || !updateBranchForm.getProvinceId().equals(branch.getProvince().getId())
        || !updateBranchForm.getDistrictId().equals(branch.getDistrict().getId())
        || !updateBranchForm.getWardId().equals(branch.getWard().getId())){
      Nation[] validatedAddress = getValidatedAddress(
          updateBranchForm.getProvinceId(),
          updateBranchForm.getDistrictId(),
          updateBranchForm.getWardId()
      );

      branch.setProvince(validatedAddress[0]);
      branch.setDistrict(validatedAddress[1]);
      branch.setWard(validatedAddress[2]);
    }
    branchMapper.fromUpdateBranchFormToEntity(updateBranchForm, branch);
    branchRepository.save(branch);
    apiMessageDto.setMessage("Update branch success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BR_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));
    Boolean existBooking = bookingRepository.existsByBranchId(id);
    if (existBooking){
      branch.setStatus(BarberConstant.BRANCH_STATUS_INACTIVE);
      branchRepository.save(branch);
    } else {
      branchRepository.delete(branch);
    }
    apiMessageDto.setMessage("Delete branch success");
    return apiMessageDto;
  }

  private Nation[] getValidatedAddress(Long provinceId, Long districtId, Long wardId) {
    Nation province = nationRepository.findById(provinceId)
        .orElseThrow(() -> new NotFoundException("Province not found", ErrorCode.NATION_ERROR_NOT_FOUND));
    if (!province.getKind().equals(BarberConstant.NATION_KIND_PROVINCE)) {
      throw new BadRequestException("Invalid Province kind", ErrorCode.NATION_ERROR_INVALID_KIND);
    }

    Nation district = nationRepository.findById(districtId)
        .orElseThrow(() -> new NotFoundException("District not found", ErrorCode.NATION_ERROR_NOT_FOUND));
    if (!district.getKind().equals(BarberConstant.NATION_KIND_DISTRICT) ||
        district.getParent() == null || !district.getParent().getId().equals(provinceId)) {
      throw new BadRequestException("District parent must be province", ErrorCode.NATION_ERROR_NOT_PARENT_PROVINCE);
    }

    Nation ward = nationRepository.findById(wardId)
        .orElseThrow(() -> new NotFoundException("Ward not found", ErrorCode.NATION_ERROR_NOT_FOUND));
    if (!ward.getKind().equals(BarberConstant.NATION_KIND_WARD) ||
        ward.getParent() == null || !ward.getParent().getId().equals(districtId)) {
      throw new BadRequestException("Ward parent must be district", ErrorCode.NATION_ERROR_NOT_PARENT_DISTRICT);
    }

    return new Nation[]{province, district, ward};
  }
}
