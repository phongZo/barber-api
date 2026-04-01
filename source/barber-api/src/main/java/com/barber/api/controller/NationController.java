package com.barber.api.controller;

import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.nation.NationAdminDto;
import com.barber.api.dto.nation.NationDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.form.nation.CreateNationForm;
import com.barber.api.form.nation.UpdateNationForm;
import com.barber.api.mapper.NationMapper;
import com.barber.api.model.Nation;
import com.barber.api.model.criteria.NationCriteria;
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
@RequestMapping("/v1/nation")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class NationController extends ABasicController{
  @Autowired
  NationRepository nationRepository;

  @Autowired
  BranchRepository branchRepository;

  @Autowired
  NationMapper nationMapper;

  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_C')")
  public ApiMessageDto<String> create(@Valid @RequestBody CreateNationForm createNationForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    validateNationKind(createNationForm.getKind(), createNationForm.getParentId());
    validateNationName(createNationForm.getName(), createNationForm.getKind(), createNationForm.getParentId(), null);
    Nation nation = nationMapper.fromCreateNationFormToEntity(createNationForm);
    if (createNationForm.getParentId() != null){
      Nation parent = nationRepository.findById(createNationForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.NATION_ERROR_NOT_FOUND));
      nation.setParent(parent);
    }
    nationRepository.save(nation);
    apiMessageDto.setMessage("Create nation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_L')")
  public ApiMessageDto<ResponseListDto<List<NationAdminDto>>> listByAdmin(NationCriteria nationCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<NationAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<NationAdminDto>> responseListDto = new ResponseListDto<>();
    Page<Nation> nations = nationRepository.findAll(nationCriteria.getSpecification(), pageable);
    List<NationAdminDto> nationAdminDtos = nationMapper.fromEntityToNationAdminDtoList(nations.getContent());
    responseListDto.setContent(nationAdminDtos);
    responseListDto.setTotalElements(nations.getTotalElements());
    responseListDto.setTotalPages(nations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list nation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<NationDto>>> listByClient(NationCriteria nationCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<NationDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<NationDto>> responseListDto = new ResponseListDto<>();
    Page<Nation> nations = nationRepository.findAll(nationCriteria.getSpecification(), pageable);
    List<NationDto> nationDtos = nationMapper.fromEntityToNationDtoList(nations.getContent());
    responseListDto.setContent(nationDtos);
    responseListDto.setTotalElements(nations.getTotalElements());
    responseListDto.setTotalPages(nations.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list nation success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_V')")
  public ApiMessageDto<NationAdminDto> getByAdmin(@PathVariable("id") Long id){
    ApiMessageDto<NationAdminDto> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Nation not found", ErrorCode.NATION_ERROR_NOT_FOUND));
    NationAdminDto nationAdminDto = nationMapper.fromEntityToNationAdminDto(nation);
    apiMessageDto.setData(nationAdminDto);
    apiMessageDto.setMessage("Get detail nation success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateNationForm updateNationForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(updateNationForm.getId())
        .orElseThrow(() -> new NotFoundException("Nation not found", ErrorCode.NATION_ERROR_NOT_FOUND));
    validateNationKind(updateNationForm.getKind(), updateNationForm.getParentId());

    if (!updateNationForm.getName().equals(nation.getName())){
      validateNationName(updateNationForm.getName(), updateNationForm.getKind(), updateNationForm.getParentId(), nation.getId());
    }

    nationMapper.fromUpdateNationFormToEntity(updateNationForm, nation);
    if (updateNationForm.getParentId() != null && !updateNationForm.getParentId().equals(nation.getParent().getId())){
      Nation parent = nationRepository.findById(updateNationForm.getParentId())
          .orElseThrow(() -> new NotFoundException("Parent not found", ErrorCode.NATION_ERROR_NOT_FOUND));
      nation.setParent(parent);
    }
    nationRepository.save(nation);
    apiMessageDto.setMessage("Update nation success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('NA_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Nation nation = nationRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Nation not found", ErrorCode.NATION_ERROR_NOT_FOUND));

    if (nation.getKind().equals(BarberConstant.NATION_KIND_PROVINCE)) {
      branchRepository.setNullProvinceByProvinceId(id);
      nationRepository.setNullChildrenByParentId(id);

    } else if (nation.getKind().equals(BarberConstant.NATION_KIND_DISTRICT)) {
      branchRepository.setNullDistrictByDistrictId(id);
      nationRepository.setNullChildrenByParentId(id);

    } else if (nation.getKind().equals(BarberConstant.NATION_KIND_WARD)) {
      branchRepository.setNullWardByWardId(id);
    }

    nationRepository.delete(nation);
    apiMessageDto.setMessage("Delete nation success");
    return apiMessageDto;
  }

  private void validateNationKind(Integer kind, Long parentId) {
    if (kind.equals(BarberConstant.NATION_KIND_PROVINCE)) {
      if (parentId != null) {
        throw new BadRequestException("Province must not have parent", ErrorCode.NATION_ERROR_NOT_PARENT);
      }
    } else {
      if (parentId == null) {
        throw new BadRequestException("Parent id cannot be null", ErrorCode.NATION_ERROR_NOT_FOUND);
      }

      Nation parent = nationRepository.findById(parentId)
          .orElseThrow(() -> new NotFoundException("Parent nation not found", ErrorCode.NATION_ERROR_NOT_FOUND));

      if (kind.equals(BarberConstant.NATION_KIND_DISTRICT) && !parent.getKind().equals(BarberConstant.NATION_KIND_PROVINCE)) {
        throw new BadRequestException("District parent must be province", ErrorCode.NATION_ERROR_NOT_PARENT_PROVINCE);
      }

      if (kind.equals(BarberConstant.NATION_KIND_WARD) && !parent.getKind().equals(BarberConstant.NATION_KIND_DISTRICT)) {
        throw new BadRequestException("Ward parent must be district", ErrorCode.NATION_ERROR_NOT_PARENT_DISTRICT);
      }
    }
  }

  private void validateNationName(String name, Integer kind, Long parentId, Long currentId) {
    boolean exists;
    if (kind.equals(BarberConstant.NATION_KIND_PROVINCE)) {
      exists = currentId == null
          ? nationRepository.existsByNameAndKind(name, kind)
          : nationRepository.existsByNameAndKindAndIdNot(name, kind, currentId);
    } else {
      exists = currentId == null
          ? nationRepository.existsByNameAndParentId(name, parentId)
          : nationRepository.existsByNameAndParentIdAndIdNot(name, parentId, currentId);
    }

    if (exists) {
      throw new BadRequestException("Nation name already exists at this kind", ErrorCode.NATION_ERROR_EXIST);
    }
  }
}
