package com.barber.api.controller;

import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.time.SlotDto;
import com.barber.api.exception.NotFoundException;
import com.barber.api.model.Branch;
import com.barber.api.repository.BranchRepository;
import com.barber.api.service.ScheduleService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/schedule")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ScheduleController extends ABasicController{
  @Autowired
  BranchRepository branchRepository;

  @Autowired
  ScheduleService scheduleService;

  @GetMapping(value = "client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<List<SlotDto>> listByClient(@RequestParam("branchId") Long branchId){
    ApiMessageDto<List<SlotDto>> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(branchId)
        .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));
    List<SlotDto> slotDtos = scheduleService.getAvailableSlot(branch);
    apiMessageDto.setData(slotDtos);
    apiMessageDto.setMessage("Get list scheduler success");
    return apiMessageDto;
  }
}
