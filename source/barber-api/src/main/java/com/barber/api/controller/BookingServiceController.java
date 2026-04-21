package com.barber.api.controller;

import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.bookingService.BookingServiceAdminDto;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.mapper.BookingServiceMapper;
import com.barber.api.model.BookingService;
import com.barber.api.model.criteria.BookingServiceCriteria;
import com.barber.api.repository.BookingServiceRepository;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/booking-service")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class BookingServiceController extends ABasicController{
  @Autowired
  BookingServiceRepository bookingServiceRepository;

  @Autowired
  BookingServiceMapper bookingServiceMapper;

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BKS_L')")
  public ApiMessageDto<ResponseListDto<List<BookingServiceAdminDto>>> listByAdmin(
      BookingServiceCriteria bookingServiceCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<BookingServiceAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BookingServiceAdminDto>> responseListDto = new ResponseListDto<>();
    Page<BookingService> bookingServices = bookingServiceRepository.findAll(bookingServiceCriteria.getSpecification(), pageable);
    List<BookingServiceAdminDto> bookingServiceAdminDtos = bookingServiceMapper.fromEntityToBookingServiceAdminDtoList(bookingServices.getContent());
    responseListDto.setContent(bookingServiceAdminDtos);
    responseListDto.setTotalElements(bookingServices.getTotalElements());
    responseListDto.setTotalPages(bookingServices.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list booking service success");
    return apiMessageDto;
  }
}
