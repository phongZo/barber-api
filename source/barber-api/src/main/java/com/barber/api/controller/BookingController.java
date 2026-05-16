package com.barber.api.controller;

import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.booking.BookingAdminDto;
import com.barber.api.dto.booking.BookingCustomerInfoDto;
import com.barber.api.dto.booking.BookingDto;
import com.barber.api.dto.bookingService.BookingServiceAdminDto;
import com.barber.api.dto.bookingService.BookingServiceDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.booking.CancelBookingForm;
import com.barber.api.form.booking.CreateBookingForm;
import com.barber.api.form.booking.UpdateStatusBookingForm;
import com.barber.api.form.bookingService.ServiceInfoForm;
import com.barber.api.mapper.BookingMapper;
import com.barber.api.mapper.BookingServiceMapper;
import com.barber.api.model.Booking;
import com.barber.api.model.BookingService;
import com.barber.api.model.Branch;
import com.barber.api.model.Customer;
import com.barber.api.model.Service;
import com.barber.api.model.criteria.BookingCriteria;
import com.barber.api.model.criteria.BookingServiceCriteria;
import com.barber.api.repository.BookingRepository;
import com.barber.api.repository.BookingServiceRepository;
import com.barber.api.repository.BranchRepository;
import com.barber.api.repository.CustomerRepository;
import com.barber.api.repository.ServiceRepository;
import com.barber.api.service.BarberApiService;
import com.barber.api.utils.JsonUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  @Autowired
  BookingServiceMapper bookingServiceMapper;

  @Autowired
  BarberApiService barberApiService;

  @PostMapping(value = "/client-create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<String> clientCreate(@Valid @RequestBody CreateBookingForm createBookingForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Branch branch = branchRepository.findById(createBookingForm.getBranchId())
        .orElseThrow(() -> new NotFoundException("Branch not found", ErrorCode.BRANCH_ERROR_NOT_FOUND));
    if (Objects.equals(branch.getStatus(), BarberConstant.BRANCH_STATUS_INACTIVE)){
      throw new BadRequestException("Branch inactive", ErrorCode.BRANCH_ERROR_INACTIVE);
    }

    List<Service> services = serviceRepository.findByIdIn(createBookingForm.getServiceIds());
    if (services.size() != createBookingForm.getServiceIds().size()) {
      throw new NotFoundException("Service not found", ErrorCode.SERVICE_ERROR_NOT_FOUND);
    }

    Long customerId = getCurrentUser() != null ? getCurrentUser() : null;
    String email = createBookingForm.getCustomerInfo() != null ? createBookingForm.getCustomerInfo().getEmail() : null;

    if (customerId == null && StringUtils.isEmpty(email)){
      throw new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
    }

    LocalDate date = LocalDate.parse(createBookingForm.getDay(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    LocalTime time = LocalTime.parse(createBookingForm.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
    ZonedDateTime vnDateTime = ZonedDateTime.of(date, time, BarberConstant.VN_ZONE);
    ZonedDateTime now = ZonedDateTime.now(BarberConstant.VN_ZONE);

    if (vnDateTime.isBefore(now)) {
      throw new BadRequestException("Booking date cannot be in the past", ErrorCode.BOOKING_ERROR_INVALID_TIME);
    }
    Date bookingDate = Date.from(vnDateTime.toInstant());

    Boolean existBooking = (customerId != null)
        ? bookingRepository.existsByCustomerIdAndBookingDateAndStatusIn(customerId, bookingDate, BarberConstant.CHECK_BOOKING_STATUS)
        : bookingRepository.existsByEmailAndBookingDateAndStatus(email, bookingDate, BarberConstant.CHECK_BOOKING_STATUS) > 0;

    if (existBooking){
      throw new BadRequestException("Booking already exist in day", ErrorCode.BOOKING_ERROR_EXIST);
    }

    Booking booking = new Booking();
    booking.setBranch(branch);
    booking.setDiscount(createBookingForm.getDiscount());
    booking.setBookingDate(bookingDate);

    boolean isGuest = customerId == null;

    if (!isGuest){
      Customer customer = customerRepository.findById(customerId)
          .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
      booking.setCustomer(customer);
      booking.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
    } else if (StringUtils.isNotEmpty(email)) {
      booking.setCustomerInfo(JsonUtils.convertJsonToString(createBookingForm.getCustomerInfo()));
      booking.setStatus(BarberConstant.BOOKING_STATUS_PENDING);
    }

    bookingRepository.save(booking);

    if (isGuest) {
      sendSuccessBooking(createBookingForm.getCustomerInfo().getEmail());
    }

    double totalPrice = 0;

    for (Service service : services){
      double price = service.getPrice() - (service.getPrice() * (service.getSaleOff() / 100));

      BookingService bookingService = new BookingService();
      bookingService.setServiceId(service.getId());
      bookingService.setBooking(booking);
      bookingService.setPrice(price);

      ServiceInfoForm serviceInfoForm = new ServiceInfoForm();
      serviceInfoForm.setName(service.getName());
      serviceInfoForm.setPrice(service.getPrice());
      serviceInfoForm.setSaleOff(service.getSaleOff());

      bookingService.setServiceInfo(JsonUtils.convertJsonToString(serviceInfoForm));
      bookingServiceRepository.save(bookingService);

      totalPrice += price;
    }

    if (createBookingForm.getDiscount() != null){
      totalPrice -= totalPrice * (createBookingForm.getDiscount() / 100);
    }

    booking.setTotalPrice(totalPrice);
    bookingRepository.save(booking);
    apiMessageDto.setMessage(isGuest ? "Check email to confirm booking" : "Create booking success");
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
  public ApiMessageDto<BookingAdminDto> getByAdmin(@PathVariable("id") Long id) {
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<BookingAdminDto> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));
    BookingAdminDto bookingAdminDto = bookingMapper.fromEntityToBookingAdminDto(booking);

    BookingServiceCriteria bookingServiceCriteria = new BookingServiceCriteria();
    bookingServiceCriteria.setBookingId(booking.getId());

    if (booking.getCustomer() != null){
      bookingServiceCriteria.setCustomerId(booking.getCustomer().getId());
    } else {
      BookingCustomerInfoDto customerInfoDto =
          JsonUtils.convertJsonStringToClass(booking.getCustomerInfo(), BookingCustomerInfoDto.class);
      bookingServiceCriteria.setEmail(customerInfoDto.getEmail());
    }

    Pageable pageable = PageRequest.of(0, 10);
    Page<BookingService> bookingServices =
        bookingServiceRepository.findAll(bookingServiceCriteria.getSpecification(), pageable);

    ResponseListDto<List<BookingServiceAdminDto>> responseListDto = new ResponseListDto<>();
    responseListDto.setContent(
        bookingServiceMapper.fromEntityToBookingServiceAdminDtoList(bookingServices.getContent())
    );
    responseListDto.setTotalElements(bookingServices.getTotalElements());
    responseListDto.setTotalPages(bookingServices.getTotalPages());

    bookingAdminDto.setBookingServices(responseListDto);
    apiMessageDto.setData(bookingAdminDto);
    apiMessageDto.setMessage("Get detail booking success");
    return apiMessageDto;
  }

  @GetMapping(value = "/client-get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<BookingDto> getByClient(@PathVariable("id") Long id) {
    ApiMessageDto<BookingDto> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));
    BookingServiceCriteria bookingServiceCriteria = new BookingServiceCriteria();
    bookingServiceCriteria.setBookingId(id);

    if (getCurrentUser() != null){
      bookingServiceCriteria.setCustomerId(getCurrentUser());
    } else {
      BookingCustomerInfoDto bookingCustomerInfoDto =
          JsonUtils.convertJsonStringToClass(booking.getCustomerInfo(), BookingCustomerInfoDto.class);
      bookingServiceCriteria.setEmail(bookingCustomerInfoDto.getEmail());
    }

    Pageable pageable = PageRequest.of(0, 10);
    Page<BookingService> bookingServices =
        bookingServiceRepository.findAll(bookingServiceCriteria.getSpecification(), pageable);

    ResponseListDto<List<BookingServiceDto>> responseListDto = new ResponseListDto<>();
    List<BookingServiceDto> bookingServiceDtos =
        bookingServiceMapper.fromEntityToBookingServiceDtoList(bookingServices.getContent());
    responseListDto.setContent(bookingServiceDtos);
    responseListDto.setTotalElements(bookingServices.getTotalElements());
    responseListDto.setTotalPages(bookingServices.getTotalPages());

    BookingDto bookingDto = bookingMapper.fromEntityToBookingDto(booking);
    bookingDto.setBookingServices(responseListDto);
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

    switch (booking.getStatus()) {
      case BarberConstant.BOOKING_CASE_STATUS_PENDING:
        if (updateStatusBookingForm.getStatus().equals(BarberConstant.BOOKING_STATUS_COMPLETED)) {
          throw new BadRequestException("Invalid status transition", ErrorCode.BOOKING_ERROR_INVALID_STATUS);
        }
        break;
      case BarberConstant.BOOKING_CASE_STATUS_BOOKING:
        if (!updateStatusBookingForm.getStatus().equals(BarberConstant.BOOKING_STATUS_COMPLETED) &&
            !updateStatusBookingForm.getStatus().equals(BarberConstant.BOOKING_STATUS_CANCELED)) {
          throw new BadRequestException("Invalid status transition", ErrorCode.BOOKING_ERROR_INVALID_STATUS);
        }
        break;
      case BarberConstant.BOOKING_CASE_STATUS_COMPLETED:
      case BarberConstant.BOOKING_CASE_STATUS_CANCELED:
        throw new BadRequestException("Invalid status transition", ErrorCode.BOOKING_ERROR_INVALID_STATUS);
      default:
        throw new BadRequestException("Unknown status", ErrorCode.BOOKING_ERROR_INVALID_STATUS);
    }

    booking.setStatus(updateStatusBookingForm.getStatus());
    bookingRepository.save(booking);
    return apiMessageDto;
  }

  @PutMapping(value = "/client-cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<String> cancelBookingByClient(@Valid @RequestBody CancelBookingForm cancelBookingForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Booking booking = bookingRepository.findById(cancelBookingForm.getId())
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));

    Customer customer = customerRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
    booking.setStatus(BarberConstant.BOOKING_STATUS_CANCELED);
    bookingRepository.save(booking);

    apiMessageDto.setMessage("Cancel booking success");
    return apiMessageDto;
  }

  private void sendSuccessBooking(String email){
    String subject = "LUXEBARBER - Đặt lịch thành công";

    String html = "<html>" +
        "<body style=\"margin:0;padding:0;background-color:#f5f5f5;font-family:'Segoe UI',Arial,sans-serif;\">" +

        "  <div style=\"max-width:600px;margin:40px auto;background:#ffffff;border-radius:20px;overflow:hidden;border:1px solid #e5e7eb;\">" +

        "    <div style=\"background:#000000;padding:40px 20px;text-align:center;\">" +
        "      <h1 style=\"margin:0;font-size:30px;font-weight:800;letter-spacing:4px;color:#ffffff;\">" +
        "        LUXE<span style=\"color:#8B0000;\">BARBER</span>" +
        "      </h1>" +
        "    </div>" +

        "    <div style=\"padding:50px 35px;text-align:center;\">" +

        "      <div style=\"width:80px;height:80px;margin:0 auto 30px auto;" +
        "                  background:#ecfdf3;border-radius:50%;line-height:80px;" +
        "                  font-size:40px;color:#22c55e;font-weight:bold;\">" +
        "        ✓" +
        "      </div>" +

        "      <h2 style=\"margin:0 0 20px 0;font-size:28px;font-weight:700;color:#111827;\">" +
        "        Đặt lịch thành công" +
        "      </h2>" +

        "      <p style=\"margin:0;font-size:16px;line-height:1.8;color:#4b5563;\">" +
        "        Lịch hẹn của quý khách đã được xác nhận trên hệ thống.<br>" +
        "        Vui lòng đến đúng giờ để được phục vụ tốt nhất." +
        "      </p>" +

        "    </div>" +

        "    <div style=\"background:#fafafa;padding:20px;text-align:center;border-top:1px solid #e5e7eb;\">" +
        "      <p style=\"margin:0;font-size:12px;color:#9ca3af;\">" +
        "        © 2026 LUXEBARBER. All rights reserved." +
        "      </p>" +
        "    </div>" +

        "  </div>" +

        "</body>" +
        "</html>";

    barberApiService.sendEmail(email, html, subject, true);
  }
}
