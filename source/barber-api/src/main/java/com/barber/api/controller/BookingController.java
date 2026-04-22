package com.barber.api.controller;

import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.booking.BookingAdminDto;
import com.barber.api.dto.booking.BookingDto;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.booking.BookingCustomerInfoForm;
import com.barber.api.form.booking.CancelBookingForm;
import com.barber.api.form.booking.CreateBookingForm;
import com.barber.api.form.booking.UpdateStatusBookingForm;
import com.barber.api.form.bookingService.ServiceInfoForm;
import com.barber.api.mapper.BookingMapper;
import com.barber.api.model.Booking;
import com.barber.api.model.BookingService;
import com.barber.api.model.Branch;
import com.barber.api.model.Customer;
import com.barber.api.model.Service;
import com.barber.api.model.criteria.BookingCriteria;
import com.barber.api.repository.BookingRepository;
import com.barber.api.repository.BookingServiceRepository;
import com.barber.api.repository.BranchRepository;
import com.barber.api.repository.CustomerRepository;
import com.barber.api.repository.ServiceRepository;
import com.barber.api.utils.JsonUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/booking")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class BookingController extends ABasicController{
  @Autowired
  BookingRepository bookingRepository;

  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  BranchRepository branchRepository;

  @Autowired
  ServiceRepository serviceRepository;

  @Autowired
  BookingServiceRepository bookingServiceRepository;

  @Autowired
  BookingMapper bookingMapper;

  @PostMapping(value = "/client-create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<String> clientCreate(@Valid @RequestBody CreateBookingForm createBookingForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(createBookingForm.getBranchId())
        .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));

    Booking booking = new Booking();
    if (getCurrentUser() != null){
      Customer customer = customerRepository.findById(getCurrentUser())
          .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
      booking.setCustomer(customer);
    } else if (createBookingForm.getCustomerInfo() != null) {
      booking.setCustomerInfo(JsonUtils.convertJsonToString(createBookingForm.getCustomerInfo()));
    } else {
      throw new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
    }
    booking.setBranch(branch);

    LocalDate date = LocalDate.parse(createBookingForm.getDay(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    LocalTime time = LocalTime.parse(createBookingForm.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
    ZonedDateTime vnDateTime = ZonedDateTime.of(date, time, BarberConstant.VN_ZONE);
    booking.setBookingDate(Date.from(vnDateTime.toInstant()));
    booking.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
    bookingRepository.save(booking);

    List<Service> services = serviceRepository.findByIdIn(createBookingForm.getServiceIds());
    double totalPrice = 0;

    for (Service service : services){
      BookingService bookingService = new BookingService();
      bookingService.setServiceId(service.getId());
      bookingService.setBooking(booking);
      bookingService.setPrice(service.getPrice() - (service.getPrice() * (service.getSaleOff() / 100)));

      ServiceInfoForm serviceInfoForm = new ServiceInfoForm();
      serviceInfoForm.setName(service.getName());
      serviceInfoForm.setPrice(service.getPrice());
      serviceInfoForm.setSaleOff(service.getSaleOff());

      bookingService.setServiceInfo(JsonUtils.convertJsonToString(serviceInfoForm));
      bookingServiceRepository.save(bookingService);
      totalPrice += bookingService.getPrice();
    }

    if (createBookingForm.getDiscount() != null){
      totalPrice -= totalPrice * (createBookingForm.getDiscount() / 100);
    }
    booking.setTotalPrice(totalPrice);
    bookingRepository.save(booking);
    apiMessageDto.setMessage("Create booking success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BK_L')")
  public ApiMessageDto<ResponseListDto<List<BookingAdminDto>>> listByAdmin(BookingCriteria bookingCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<BookingAdminDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BookingAdminDto>> responseListDto = new ResponseListDto<>();
    Page<Booking> bookings = bookingRepository.findAll(bookingCriteria.getSpecification(), pageable);
    List<BookingAdminDto> bookingAdminDtos = bookingMapper.fromEntityToBookingAdminDtoList(bookings.getContent());
    responseListDto.setContent(bookingAdminDtos);
    responseListDto.setTotalElements(bookings.getTotalElements());
    responseListDto.setTotalPages(bookings.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list booking success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-list", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<ResponseListDto<List<BookingDto>>> listByClient(BookingCriteria bookingCriteria, Pageable pageable){
    ApiMessageDto<ResponseListDto<List<BookingDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<BookingDto>> responseListDto = new ResponseListDto<>();
    if (getCurrentUser() != null){
      bookingCriteria.setCustomerId(getCurrentUser());
    }
    Page<Booking> bookings = bookingRepository.findAll(bookingCriteria.getSpecification(), pageable);
    List<BookingDto> bookingAdminDtos = bookingMapper.fromEntityToBookingDtoList(bookings.getContent());
    responseListDto.setContent(bookingAdminDtos);
    responseListDto.setTotalElements(bookings.getTotalElements());
    responseListDto.setTotalPages(bookings.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list booking success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BK_V')")
  public ApiMessageDto<BookingAdminDto> getByAdmin(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<BookingAdminDto> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));
    BookingAdminDto bookingAdminDto = bookingMapper.fromEntityToBookingAdminDto(booking);
    apiMessageDto.setData(bookingAdminDto);
    apiMessageDto.setMessage("Get detail booking success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<BookingDto> getByClient(@PathVariable("id") Long id){
    ApiMessageDto<BookingDto> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));
    BookingDto bookingDto = bookingMapper.fromEntityToBookingDto(booking);
    apiMessageDto.setData(bookingDto);
    apiMessageDto.setMessage("Get detail booking success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update-status", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('BK_UST')")
  public ApiMessageDto<String> updateStatus(@Valid @RequestBody UpdateStatusBookingForm updateStatusBookingForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(updateStatusBookingForm.getId())
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));
    booking.setStatus(updateStatusBookingForm.getStatus());
    bookingRepository.save(booking);
    return apiMessageDto;
  }

  @PutMapping(value = "/client-cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<String> cancelBookingByClient(@Valid @RequestBody CancelBookingForm cancelBookingForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(cancelBookingForm.getId())
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));
    if (getCurrentUser() != null){
      Customer customer = customerRepository.findById(getCurrentUser())
          .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
      booking.setStatus(BarberConstant.BOOKING_STATUS_CANCELED);
    } else if (StringUtils.isNotBlank(cancelBookingForm.getPhone())){
      BookingCustomerInfoForm customerInfoForm = JsonUtils.convertJsonStringToClass(booking.getCustomerInfo(), BookingCustomerInfoForm.class);
      if (!cancelBookingForm.getPhone().equals(customerInfoForm.getPhone())){
        throw new UnauthorizationException("Customer cannot cancel booking");
      }
      booking.setStatus(BarberConstant.BOOKING_STATUS_CANCELED);
    } else {
      throw new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
    }

    bookingRepository.save(booking);
    apiMessageDto.setMessage("Cancel booking success");
    return apiMessageDto;
  }
}
