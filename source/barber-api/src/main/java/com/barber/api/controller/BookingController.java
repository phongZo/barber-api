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
import com.barber.api.form.booking.BookingCustomerInfoForm;
import com.barber.api.form.booking.BookingTokenRequestForm;
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
import com.barber.api.utils.BookingTokenUtils;
import com.barber.api.utils.ConvertUtils;
import com.barber.api.utils.JsonUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    List<Service> services = serviceRepository.findByIdIn(createBookingForm.getServiceIds());
    if (services.size() != createBookingForm.getServiceIds().size()) {
      throw new NotFoundException("Service not found", ErrorCode.SERVICE_ERROR_NOT_FOUND);
    }

    Booking booking = new Booking();
    booking.setBranch(branch);
    booking.setDiscount(createBookingForm.getDiscount());

    boolean isGuest = true;

    if (getCurrentUser() != null){
      Customer customer = customerRepository.findById(getCurrentUser())
          .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
      booking.setCustomer(customer);
      isGuest = false;
    } else if (createBookingForm.getCustomerInfo() != null) {
      booking.setCustomerInfo(JsonUtils.convertJsonToString(createBookingForm.getCustomerInfo()));
    } else {
      throw new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
    }

    LocalDate date = LocalDate.parse(createBookingForm.getDay(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    LocalTime time = LocalTime.parse(createBookingForm.getTime(), DateTimeFormatter.ofPattern("HH:mm"));
    ZonedDateTime vnDateTime = ZonedDateTime.of(date, time, BarberConstant.VN_ZONE);

    booking.setBookingDate(Date.from(vnDateTime.toInstant()));

    booking.setStatus(isGuest ? BarberConstant.BOOKING_STATUS_PENDING : BarberConstant.BOOKING_STATUS_BOOKING);

    bookingRepository.save(booking);

    if (isGuest) {
      String token = BookingTokenUtils.generateToken(
          createBookingForm.getCustomerInfo().getEmail(),
          booking.getId(),
          BarberConstant.BOOKING_ACTION_CREATE
      );
      sendConfirmCreateEmail(createBookingForm.getCustomerInfo().getEmail(), token);
    }

    double totalPrice = 0;

    for (Service service : services){
      BookingService bookingService = new BookingService();
      bookingService.setServiceId(service.getId());
      bookingService.setBooking(booking);

      double price = service.getPrice() - (service.getPrice() * (service.getSaleOff() / 100));
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
        if (!updateStatusBookingForm.getStatus().equals(BarberConstant.BOOKING_STATUS_BOOKING)) {
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

    Boolean isGuest = true;
    if (getCurrentUser() != null){
      Customer customer = customerRepository.findById(getCurrentUser())
              .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
      booking.setStatus(BarberConstant.BOOKING_STATUS_CANCELED);
      isGuest = false;
      bookingRepository.save(booking);
    } else if (StringUtils.isNotEmpty(cancelBookingForm.getEmail())){
      BookingCustomerInfoForm info = JsonUtils.convertJsonStringToClass(booking.getCustomerInfo(), BookingCustomerInfoForm.class);

      if (!cancelBookingForm.getEmail().equals(info.getEmail())){
        throw new UnauthorizationException("Cannot cancel booking");
      }

      String token = BookingTokenUtils.generateToken(
          info.getEmail(),
          booking.getId(),
          BarberConstant.BOOKING_ACTION_CANCEL
      );

      sendConfirmCancelEmail(info.getEmail(), token);
    } else{
      throw new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND);
    }

    apiMessageDto.setMessage(isGuest ? "Check email to confirm cancel" : "Cancel booking success");
    return apiMessageDto;
  }

  @PutMapping("/confirm-create")
  public ApiMessageDto<String> confirmCreate(@Valid @RequestBody BookingTokenRequestForm bookingTokenRequestForm, BindingResult bindingResult) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

    Map<String, Object> data = BookingTokenUtils.parseToken(bookingTokenRequestForm.getToken());

    Long bookingId = ConvertUtils.convertStringToLong(data.get("bookingId").toString());
    String email = data.get("email").toString();
    String action = data.get("action").toString();
    Long exp = ConvertUtils.convertStringToLong(data.get("exp").toString());

    if (!BarberConstant.BOOKING_ACTION_CREATE.equalsIgnoreCase(action)) {
      throw new BadRequestException("Invalid action", ErrorCode.BOOKING_ERROR_INVALID_ACTION);
    }

    if (System.currentTimeMillis() > exp) {
      throw new BadRequestException("Token expired", ErrorCode.BOOKING_ERROR_TOKEN_EXPIRED);
    }

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));

    BookingCustomerInfoForm info = JsonUtils.convertJsonStringToClass(booking.getCustomerInfo(), BookingCustomerInfoForm.class);

    if (!email.equals(info.getEmail())) {
      throw new BadRequestException("Invalid email", ErrorCode.BOOKING_ERROR_INVALID_EMAIL);
    }

    booking.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
    bookingRepository.save(booking);

    apiMessageDto.setMessage("Booking confirmed");
    return apiMessageDto;
  }

  @PutMapping("/confirm-cancel")
  public ApiMessageDto<String> confirmCancel(@Valid @RequestBody BookingTokenRequestForm bookingTokenRequestForm, BindingResult bindingResult) {
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Map<String, Object> data = BookingTokenUtils.parseToken(bookingTokenRequestForm.getToken());

    Long bookingId = ConvertUtils.convertStringToLong(data.get("bookingId").toString());
    String email = data.get("email").toString();
    String action = data.get("action").toString();
    Long exp = ConvertUtils.convertStringToLong(data.get("exp").toString());

    if (!BarberConstant.BOOKING_ACTION_CANCEL.equalsIgnoreCase(action)) {
      throw new BadRequestException("Invalid action", ErrorCode.BOOKING_ERROR_INVALID_ACTION);
    }

    if (System.currentTimeMillis() > exp) {
      throw new BadRequestException("Token expired", ErrorCode.BOOKING_ERROR_TOKEN_EXPIRED);
    }

    Booking booking = bookingRepository.findById(bookingId)
        .orElseThrow(() -> new NotFoundException("Booking not found", ErrorCode.BOOKING_ERROR_NOT_FOUND));

    BookingCustomerInfoForm info = JsonUtils.convertJsonStringToClass(booking.getCustomerInfo(), BookingCustomerInfoForm.class);

    if (!email.equals(info.getEmail())) {
      throw new BadRequestException("Invalid email", ErrorCode.BOOKING_ERROR_INVALID_EMAIL);
    }

    booking.setStatus(BarberConstant.BOOKING_STATUS_CANCELED);
    bookingRepository.save(booking);

    apiMessageDto.setMessage("Booking canceled");
    return apiMessageDto;
  }

  private void sendConfirmCreateEmail(String email, String token){
    String subject = "Xác nhận lịch đặt";
    String link = "http://localhost:8787/v1/booking/confirm-create?token=" + token;
    String html = "<div style=\"font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; background-color: #f9f9f9;\">" +
        "<h2 style=\"color: #2c3e50; text-align: center;\">Xác nhận lịch đặt</h2>" +
        "<p style=\"font-size: 16px; line-height: 1.6;\">Bạn vừa thực hiện đặt lịch tại hệ thống.</p>" +
        "<p style=\"font-size: 16px; line-height: 1.6;\">Vui lòng nhấn nút bên dưới để xác nhận lịch của bạn:</p>" +
        "<div style=\"text-align: center; margin: 20px 0;\">" +
        "<a href=\"" + link + "\" " +
        "style=\"display: inline-block; padding: 12px 24px; font-size: 16px; color: #fff; background-color: #28a745; text-decoration: none; border-radius: 5px;\">" +
        "Xác nhận đặt lịch</a>" +
        "</div>" +
        "<p style=\"font-size: 16px; line-height: 1.6; color: #555;\">Liên kết sẽ hết hạn trong <strong>15 phút</strong>.</p>" +
        "<p style=\"font-size: 14px; color: #999; font-style: italic;\">Nếu bạn không thực hiện hành động này, vui lòng bỏ qua email.</p>" +
        "</div>";
    barberApiService.sendEmail(email, html, subject, true);
  }

  private void sendConfirmCancelEmail(String email, String token){
    String subject = "Xác nhận hủy lịch";
    String link = "http://localhost:8787/v1/booking/confirm-cancel?token=" + token;
    String html = "<div style=\"font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; background-color: #f9f9f9;\">" +
        "<h2 style=\"color: #e74c3c; text-align: center;\">Xác nhận hủy lịch</h2>" +
        "<p style=\"font-size: 16px; line-height: 1.6;\">Bạn đã yêu cầu hủy lịch đặt.</p>" +
        "<p style=\"font-size: 16px; line-height: 1.6;\">Vui lòng nhấn nút bên dưới để xác nhận hủy lịch:</p>" +
        "<div style=\"text-align: center; margin: 20px 0;\">" +
        "<a href=\"" + link + "\" " +
        "style=\"display: inline-block; padding: 12px 24px; font-size: 16px; color: #fff; background-color: #e74c3c; text-decoration: none; border-radius: 5px;\">" +
        "Xác nhận hủy lịch</a>" +
        "</div>" +
        "<p style=\"font-size: 16px; line-height: 1.6; color: #555;\">Liên kết sẽ hết hạn trong <strong>15 phút</strong>.</p>" +
        "<p style=\"font-size: 14px; color: #999; font-style: italic;\">Nếu bạn không thực hiện hành động này, vui lòng bỏ qua email.</p>" +
        "</div>";
    barberApiService.sendEmail(email, html, subject, true);
  }
}
