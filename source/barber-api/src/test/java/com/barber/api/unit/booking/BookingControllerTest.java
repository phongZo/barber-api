package com.barber.api.unit.booking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barber.api.constant.BarberConstant;
import com.barber.api.controller.BookingController;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.account.AccountDto;
import com.barber.api.dto.account.AccountUserDto;
import com.barber.api.dto.booking.BookingAdminDto;
import com.barber.api.dto.booking.BookingCustomerInfoDto;
import com.barber.api.dto.booking.BookingDto;
import com.barber.api.dto.branch.BranchAdminDto;
import com.barber.api.dto.branch.BranchDto;
import com.barber.api.dto.customer.CustomerDto;
import com.barber.api.dto.customer.CustomerProfileDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.booking.BookingCustomerInfoForm;
import com.barber.api.form.booking.CancelBookingForm;
import com.barber.api.form.booking.CreateBookingForm;
import com.barber.api.form.booking.UpdateStatusBookingForm;
import com.barber.api.jwt.BarberJwt;
import com.barber.api.mapper.BookingMapper;
import com.barber.api.model.Account;
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
import com.barber.api.service.BarberApiService;
import com.barber.api.service.impl.UserServiceImpl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {
  @InjectMocks
  BookingController bookingController;

  @Mock
  BookingRepository bookingRepository;

  @Mock
  BranchRepository branchRepository;

  @Mock
  CustomerRepository customerRepository;

  @Mock
  ServiceRepository serviceRepository;

  @Mock
  BookingServiceRepository bookingServiceRepository;

  @Mock
  BindingResult bindingResult;

  @Mock
  BookingMapper bookingMapper;

  @Mock
  UserServiceImpl userService;

  @Mock
  BarberApiService barberApiService;

  Customer mockCustomer;

  Branch mockBranch;

  Service mockService1;

  Service mockService2;

  Booking mockBooking1;

  Booking mockBooking2;

  BookingService mockBookingService1;

  BookingService mockBookingService2;

  BookingService mockBookingService3;

  BookingService mockBookingService4;

  @BeforeEach
  void initData() throws ParseException {
    Account mockAccount = new Account();
    mockAccount.setId(1L);
    mockAccount.setUsername("trunghao");
    mockAccount.setFullName("Trịnh Trung Hào");
    mockAccount.setEmail("trunghao@gmail.com");
    mockAccount.setPhone("0879738264");
    mockAccount.setKind(BarberConstant.USER_KIND_CUSTOMER);

    mockCustomer = new Customer();
    mockCustomer.setId(1L);
    mockCustomer.setAccount(mockAccount);

    mockBranch = new Branch();
    mockBranch.setId(2L);
    mockBranch.setName("Barber - 123 Nguyễn Đình Chiểu");
    mockBranch.setPhone("0892612372");
    mockBranch.setSetting("{ " + " \"openTime\" : \"08:00\", " + "   \"closeTime\" : \"20:00\"" + " }");

    mockService1 = new Service();
    mockService1.setId(3L);
    mockService1.setName("Cắt gội");
    mockService1.setPrice(122000.0);
    mockService1.setSaleOff(10.0);
    mockService1.setDuration(40);

    mockService2 = new Service();
    mockService2.setId(4L);
    mockService2.setName("Combo lấy ráy tai VIP");
    mockService2.setPrice(70000.0);
    mockService2.setSaleOff(0.0);
    mockService2.setDuration(20);

    mockBooking1 = new Booking();
    mockBooking1.setId(5L);
    mockBooking1.setDiscount(10.0);
    mockBooking1.setTotalPrice(161820.0);
    mockBooking1.setCustomer(mockCustomer);
    mockBooking1.setBranch(mockBranch);
    mockBooking1.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
    mockBooking1.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));

    mockBookingService1 = new BookingService();
    mockBookingService1.setId(6L);
    mockBookingService1.setBooking(mockBooking1);
    mockBookingService1.setPrice(109800.0);
    mockBookingService1.setServiceId(3L);
    mockBookingService1.setServiceInfo("{" + "\"name\" : \"Cắt gội\"," + "\"price\" : 122000.0,"+ "\"saleOff\" : 10.0 }");

    mockBookingService2 = new BookingService();
    mockBookingService2.setId(7L);
    mockBookingService2.setBooking(mockBooking1);
    mockBookingService2.setPrice(70000.0);
    mockBookingService2.setServiceId(4L);
    mockBookingService2.setServiceInfo("{" + "\"name\" : \"Combo lấy ráy tai VIP\"," + "\"price\" : 70000.0," + "\"saleOff\" : 0.0 }");

    mockBooking2 = new Booking();
    mockBooking2.setId(8L);
    mockBooking2.setDiscount(10.0);
    mockBooking2.setTotalPrice(161820.0);
    mockBooking2.setCustomer(null);
    mockBooking2.setCustomerInfo("{" + "  \"email\" : \"22110316@student.hcmute.edu.vn\" }");
    mockBooking2.setBranch(mockBranch);
    mockBooking2.setStatus(BarberConstant.BOOKING_STATUS_PENDING);
    mockBooking2.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("24/04/2026 10:30"));

    mockBookingService3 = new BookingService();
    mockBookingService3.setId(9L);
    mockBookingService3.setBooking(mockBooking2);
    mockBookingService3.setPrice(109800.0);
    mockBookingService3.setServiceId(3L);
    mockBookingService3.setServiceInfo("{" + "\"name\" : \"Cắt gội\"," + "\"price\" : 122000.0,"+ "\"saleOff\" : 10.0 }");

    mockBookingService4 = new BookingService();
    mockBookingService4.setId(10L);
    mockBookingService4.setBooking(mockBooking2);
    mockBookingService4.setPrice(70000.0);
    mockBookingService4.setServiceId(4L);
    mockBookingService4.setServiceInfo("{" + "\"name\" : \"Combo lấy ráy tai VIP\"," + "\"price\" : 70000.0," + "\"saleOff\" : 0.0 }");
  }

  @Nested
  @DisplayName("Admin action")
  class AdminTest{
    @BeforeEach
    void setupAdmin(){
      BarberJwt adminJwt = new BarberJwt();
      adminJwt.setUserKind(BarberConstant.USER_KIND_ADMIN);
      when(userService.getAddInfoFromToken()).thenReturn(adminJwt);

      lenient().when(userService.getAddInfoFromToken()).thenReturn(adminJwt);
    }

    @Test
    @DisplayName("Get list booking success")
    void testGetListBooking_ValidCriteria_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<Booking> bookingList = Collections.singletonList(mockBooking1);
      Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, 1);

      BookingAdminDto bookingAdminDto = new BookingAdminDto();
      bookingAdminDto.setId(5L);
      bookingAdminDto.setDiscount(10.0);
      bookingAdminDto.setTotalPrice(161820.0);
      bookingAdminDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));
      bookingAdminDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingAdminDto.setCustomerInfo(null);

      AccountDto accountDto = new AccountDto();
      accountDto.setId(1L);
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");
      accountDto.setKind(BarberConstant.USER_KIND_CUSTOMER);

      CustomerDto customerDto = new CustomerDto();
      customerDto.setId(1L);
      customerDto.setAccount(accountDto);
      bookingAdminDto.setCustomer(customerDto);

      BranchAdminDto branchDto = new BranchAdminDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingAdminDto.setBranch(branchDto);

      when(bookingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookingPage);
      List<BookingAdminDto> dtoList = Collections.singletonList(bookingAdminDto);
      when(bookingMapper.fromEntityToBookingAdminDtoList(bookingList)).thenReturn(dtoList);

      ApiMessageDto<ResponseListDto<List<BookingAdminDto>>> response = bookingController.listByAdmin(new BookingCriteria(), pageable);
      ResponseListDto<List<BookingAdminDto>> data = response.getData();
      assertEquals(1, data.getTotalElements());
      assertEquals(1, data.getTotalPages());

      List<BookingAdminDto> content = data.getContent();
      BookingAdminDto responseDto = content.get(0);
      assertEquals(mockBooking1.getId(), responseDto.getId());
      assertEquals(mockBooking1.getTotalPrice(), responseDto.getTotalPrice());
      assertEquals(mockBooking1.getBookingDate(), responseDto.getBookingDate());
      assertEquals(mockBooking1.getStatus(), responseDto.getStatus());

      verify(bookingRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingMapper, times(1)).fromEntityToBookingAdminDtoList(bookingList);
    }

    @Test
    @DisplayName("Test get list booking empty")
    void testGetListBooking_Empty_ShouldSuccess() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Booking> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

      when(bookingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(emptyPage);

      when(bookingMapper.fromEntityToBookingAdminDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());

      ApiMessageDto<ResponseListDto<List<BookingAdminDto>>> response = bookingController.listByAdmin(new BookingCriteria(), pageable);

      assertEquals(0, response.getData().getTotalElements());
      assertEquals(0, response.getData().getContent().size());

      verify(bookingRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingMapper, times(1)).fromEntityToBookingAdminDtoList(Collections.emptyList());
    }

    @Test
    @DisplayName("Get detail booking success")
    void testGetDetail_ExistingId_ShouldSuccess() throws ParseException {
      BookingAdminDto bookingAdminDto = new BookingAdminDto();
      bookingAdminDto.setId(5L);
      bookingAdminDto.setDiscount(10.0);
      bookingAdminDto.setTotalPrice(161820.0);
      bookingAdminDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));
      bookingAdminDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingAdminDto.setCustomerInfo(null);

      AccountDto accountDto = new AccountDto();
      accountDto.setId(1L);
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");
      accountDto.setKind(BarberConstant.USER_KIND_CUSTOMER);

      CustomerDto customerDto = new CustomerDto();
      customerDto.setId(1L);
      customerDto.setAccount(accountDto);
      bookingAdminDto.setCustomer(customerDto);

      BranchAdminDto branchDto = new BranchAdminDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingAdminDto.setBranch(branchDto);

      when(bookingRepository.findById(5L)).thenReturn(Optional.of(mockBooking1));
      when(bookingMapper.fromEntityToBookingAdminDto(mockBooking1)).thenReturn(bookingAdminDto);

      ApiMessageDto<BookingAdminDto> response = bookingController.getByAdmin(5L);
      BookingAdminDto responseDto = response.getData();

      assertEquals(mockBooking1.getId(), responseDto.getId());
      assertEquals(mockBooking1.getTotalPrice(), responseDto.getTotalPrice());
      assertEquals(mockBooking1.getBookingDate(), responseDto.getBookingDate());
      assertEquals(mockBooking1.getStatus(), responseDto.getStatus());

      verify(bookingRepository, times(1)).findById(5L);
      verify(bookingMapper, times(1)).fromEntityToBookingAdminDto(eq(mockBooking1));
    }

    @Test
    @DisplayName("Get detail booking not found")
    void testGetDetail_NotFound_ShouldFail() {
      when(bookingRepository.findById(11L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookingController.getByAdmin(11L));

      verify(bookingRepository, times(1)).findById(11L);
      verify(bookingMapper, never()).fromEntityToBookingAdminDto(any());
    }

    @Test
    @DisplayName("Test update status booking success")
    void testUpdateStatus_ValidForm_ShouldSuccess(){
      UpdateStatusBookingForm updateStatusBookingForm = new UpdateStatusBookingForm();
      updateStatusBookingForm.setId(5L);
      updateStatusBookingForm.setStatus(BarberConstant.BOOKING_STATUS_COMPLETED);

      when(bookingRepository.findById(updateStatusBookingForm.getId())).thenReturn(Optional.of(mockBooking1));

      ApiMessageDto<String> result = bookingController.updateStatus(updateStatusBookingForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(bookingRepository, times(1)).findById(updateStatusBookingForm.getId());
    }

    @Test
    @DisplayName("Update status fail when booking not found")
    void testUpdateStatus_BookingNotFound_ShouldFail() {
      UpdateStatusBookingForm form = new UpdateStatusBookingForm();
      form.setId(20L);
      form.setStatus(BarberConstant.BOOKING_STATUS_COMPLETED);

      when(bookingRepository.findById(20L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookingController.updateStatus(form, bindingResult));

      verify(bookingRepository, times(1)).findById(20L);
      verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update status fail when invalid transition")
    void testUpdateStatus_InvalidTransition_ShouldFail() {
      UpdateStatusBookingForm form = new UpdateStatusBookingForm();
      form.setId(5L);
      form.setStatus(BarberConstant.BOOKING_STATUS_PENDING);

      when(bookingRepository.findById(5L)).thenReturn(Optional.of(mockBooking1));

      assertThrows(BadRequestException.class, () -> bookingController.updateStatus(form, bindingResult));

      verify(bookingRepository, times(1)).findById(5L);
      verify(bookingRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Customer action")
  class CustomerTest{
    BarberJwt customerJwt;
    @BeforeEach
    void setupCustomer() {
      customerJwt = new BarberJwt();
      customerJwt.setAccountId(mockCustomer.getAccount().getId());
      customerJwt.setUsername(mockCustomer.getAccount().getUsername());
      customerJwt.setUserKind(BarberConstant.USER_KIND_CUSTOMER);

      lenient().when(userService.getAddInfoFromToken()).thenReturn(customerJwt);
    }

    @Test
    @DisplayName("Test create booking success: customer logged in")
    void testCreateBooking_ValidForm_CustomerLoggedIn_ShouldSuccess() throws ParseException {
      CreateBookingForm createBookingForm = new CreateBookingForm();
      createBookingForm.setDiscount(10.0);
      createBookingForm.setDay("25/04/2026");
      createBookingForm.setTime("14:30");
      createBookingForm.setBranchId(2L);
      createBookingForm.setServiceIds(List.of(3L, 4L));

      Booking newBooking = new Booking();
      newBooking.setCustomerInfo(null);
      newBooking.setDiscount(createBookingForm.getDiscount());
      newBooking.setTotalPrice(161820.0);
      newBooking.setBranch(mockBranch);
      newBooking.setCustomer(mockCustomer);
      newBooking.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(createBookingForm.getDay() + " " + createBookingForm.getTime()));
      newBooking.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);

      BookingService newBookingService1 = new BookingService();
      newBookingService1.setId(12L);
      newBookingService1.setBooking(newBooking);
      newBookingService1.setPrice(109800.0);
      newBookingService1.setServiceId(3L);
      newBookingService1.setServiceInfo("{" + "\"name\" : \"Cắt gội\"," + "\"price\" : 122000.0,"+ "\"saleOff\" : 10.0 }");

      BookingService newBookingService2 = new BookingService();
      newBookingService2.setId(10L);
      newBookingService2.setBooking(newBooking);
      newBookingService2.setPrice(70000.0);
      newBookingService2.setServiceId(4L);
      newBookingService2.setServiceInfo("{" + "\"name\" : \"Combo lấy ráy tai VIP\"," + "\"price\" : 70000.0," + "\"saleOff\" : 0.0 }");

      when(branchRepository.findById(createBookingForm.getBranchId())).thenReturn(Optional.of(mockBranch));
      when(customerRepository.findById(customerJwt.getAccountId())).thenReturn(Optional.of(mockCustomer));
      when(serviceRepository.findByIdIn(createBookingForm.getServiceIds())).thenReturn(List.of(mockService1, mockService2));

      when(bookingRepository.save(any(Booking.class)))
          .thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            if (b.getId() == null) {
              b.setId(11L);
            }
            return b;
          });

      when(bookingServiceRepository.save(any(BookingService.class)))
          .thenAnswer(invocation -> {
            BookingService bs = invocation.getArgument(0);

            if (bs.getId() == null) {
              if (bs.getServiceId().equals(3L)) {
                bs.setId(12L);
              } else if (bs.getServiceId().equals(4L)) {
                bs.setId(13L);
              }
            }
            return bs;
          });

      ApiMessageDto<String> result = bookingController.clientCreate(createBookingForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(branchRepository, times(1)).findById(createBookingForm.getBranchId());
      verify(serviceRepository, times(1)).findByIdIn(createBookingForm.getServiceIds());
      verify(customerRepository, times(1)).findById(customerJwt.getAccountId());
      verify(bookingRepository, times(2)).save(any());
      verify(bookingServiceRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Test create booking success: customer not logged in")
    void testCreateBooking_ValidForm_CustomerNotLoggedIn_ShouldSuccess() throws ParseException {
      BookingCustomerInfoForm customerInfoForm = new BookingCustomerInfoForm();
      customerInfoForm.setEmail("TrinhTrungHao@gmail.com");

      CreateBookingForm createBookingForm = new CreateBookingForm();
      createBookingForm.setCustomerInfo(customerInfoForm);
      createBookingForm.setDiscount(10.0);
      createBookingForm.setDay("25/04/2026");
      createBookingForm.setTime("11:30");
      createBookingForm.setBranchId(2L);
      createBookingForm.setServiceIds(List.of(3L, 4L));

      Booking newBooking = new Booking();
      newBooking.setCustomerInfo("{" + "  \"email\" : \"TrinhTrungHao@gmail.com\" }");
      newBooking.setDiscount(createBookingForm.getDiscount());
      newBooking.setTotalPrice(161820.0);
      newBooking.setBranch(mockBranch);
      newBooking.setCustomer(null);
      newBooking.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(createBookingForm.getDay() + " " + createBookingForm.getTime()));
      newBooking.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);

      BookingService newBookingService1 = new BookingService();
      newBookingService1.setId(12L);
      newBookingService1.setBooking(newBooking);
      newBookingService1.setPrice(109800.0);
      newBookingService1.setServiceId(3L);
      newBookingService1.setServiceInfo("{" + "\"name\" : \"Cắt gội\"," + "\"price\" : 122000.0,"+ "\"saleOff\" : 10.0 }");

      BookingService newBookingService2 = new BookingService();
      newBookingService2.setId(10L);
      newBookingService2.setBooking(newBooking);
      newBookingService2.setPrice(70000.0);
      newBookingService2.setServiceId(4L);
      newBookingService2.setServiceInfo("{" + "\"name\" : \"Combo lấy ráy tai VIP\"," + "\"price\" : 70000.0," + "\"saleOff\" : 0.0 }");

      when(userService.getAddInfoFromToken()).thenReturn(null);
      when(branchRepository.findById(createBookingForm.getBranchId())).thenReturn(Optional.of(mockBranch));
      when(serviceRepository.findByIdIn(createBookingForm.getServiceIds())).thenReturn(List.of(mockService1, mockService2));

      when(bookingRepository.save(any(Booking.class)))
          .thenAnswer(invocation -> {
            Booking b = invocation.getArgument(0);
            if (b.getId() == null) {
              b.setId(11L);
            }
            return b;
          });

      when(bookingServiceRepository.save(any(BookingService.class)))
          .thenAnswer(invocation -> {
            BookingService bs = invocation.getArgument(0);

            if (bs.getId() == null) {
              if (bs.getServiceId().equals(3L)) {
                bs.setId(12L);
              } else if (bs.getServiceId().equals(4L)) {
                bs.setId(13L);
              }
            }
            return bs;
          });

      ApiMessageDto<String> result = bookingController.clientCreate(createBookingForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(userService, times(1)).getAddInfoFromToken();
      verify(branchRepository, times(1)).findById(createBookingForm.getBranchId());
      verify(serviceRepository, times(1)).findByIdIn(createBookingForm.getServiceIds());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, times(2)).save(any());
      verify(bookingServiceRepository, times(2)).save(any());
    }

    @Test
    @DisplayName("Test create booking fail when branch not found")
    void testCreateBooking_BranchNotFound_ShouldFail(){
      BookingCustomerInfoForm customerInfoForm = new BookingCustomerInfoForm();
      customerInfoForm.setEmail("TrinhTrungHao@gmail.com");

      CreateBookingForm createBookingForm = new CreateBookingForm();
      createBookingForm.setCustomerInfo(customerInfoForm);
      createBookingForm.setDiscount(10.0);
      createBookingForm.setDay("25/04/2026");
      createBookingForm.setTime("11:30");
      createBookingForm.setBranchId(12L);
      createBookingForm.setServiceIds(List.of(3L, 4L));

      when(branchRepository.findById(createBookingForm.getBranchId())).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookingController.clientCreate(createBookingForm, bindingResult));

      verify(branchRepository, times(1)).findById(createBookingForm.getBranchId());
      verify(serviceRepository, never()).findByIdIn(anyList());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
      verify(bookingServiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test create booking fail when customer not found")
    void testCreateBooking_CustomerNotFound_ShouldFail(){
      CreateBookingForm createBookingForm = new CreateBookingForm();
      createBookingForm.setCustomerInfo(null);
      createBookingForm.setDiscount(10.0);
      createBookingForm.setDay("25/04/2026");
      createBookingForm.setTime("11:30");
      createBookingForm.setBranchId(2L);
      createBookingForm.setServiceIds(List.of(3L, 4L));

      when(userService.getAddInfoFromToken()).thenReturn(null);
      when(branchRepository.findById(createBookingForm.getBranchId())).thenReturn(Optional.of(mockBranch));
      when(serviceRepository.findByIdIn(createBookingForm.getServiceIds())).thenReturn(List.of(mockService1, mockService2));

      assertThrows(NotFoundException.class, () -> bookingController.clientCreate(createBookingForm, bindingResult));

      verify(userService, times(1)).getAddInfoFromToken();
      verify(branchRepository, times(1)).findById(createBookingForm.getBranchId());
      verify(serviceRepository, times(1)).findByIdIn(createBookingForm.getServiceIds());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
      verify(bookingServiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create booking fail when service not found")
    void testCreateBooking_ServiceNotFound_ShouldFail() {
      CreateBookingForm createBookingForm = new CreateBookingForm();
      createBookingForm.setDay("25/04/2026");
      createBookingForm.setTime("10:00");
      createBookingForm.setBranchId(2L);
      createBookingForm.setServiceIds(List.of(100L, 3L));

      when(branchRepository.findById(createBookingForm.getBranchId())).thenReturn(Optional.of(mockBranch));
      when(serviceRepository.findByIdIn(createBookingForm.getServiceIds())).thenReturn(Collections.emptyList());

      assertThrows(NotFoundException.class, () -> bookingController.clientCreate(createBookingForm, bindingResult));

      verify(branchRepository, times(1)).findById(createBookingForm.getBranchId());
      verify(serviceRepository, times(1)).findByIdIn(createBookingForm.getServiceIds());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
      verify(bookingServiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cancel booking success: customer logged in")
    void testCancelBooking_ValidForm_CustomerLoggedId_ShouldSuccess() {
      CancelBookingForm cancelBookingForm = new CancelBookingForm();
      cancelBookingForm.setId(5L);

      when(customerRepository.findById(customerJwt.getAccountId())).thenReturn(Optional.of(mockCustomer));
      when(bookingRepository.findById(cancelBookingForm.getId())).thenReturn(Optional.of(mockBooking1));

      ApiMessageDto<String> result = bookingController.cancelBookingByClient(cancelBookingForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(bookingRepository, times(1)).findById(cancelBookingForm.getId());
      verify(customerRepository, times(1)).findById(customerJwt.getAccountId());
      verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Cancel booking success: customer not logged in")
    void testCancelBooking_ValidForm_CustomerNotLoggedId_ShouldSuccess() {
      CancelBookingForm cancelBookingForm = new CancelBookingForm();
      cancelBookingForm.setId(8L);
      cancelBookingForm.setEmail("22110316@student.hcmute.edu.vn");

      when(userService.getAddInfoFromToken()).thenReturn(null);
      when(bookingRepository.findById(cancelBookingForm.getId())).thenReturn(Optional.of(mockBooking2));

      ApiMessageDto<String> result = bookingController.cancelBookingByClient(cancelBookingForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(bookingRepository, times(1)).findById(cancelBookingForm.getId());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test cancel booking fail when customer not found")
    void testCancelBooking_CustomerNotFound_ShouldFail(){
      CancelBookingForm cancelBookingForm = new CancelBookingForm();
      cancelBookingForm.setId(5L);
      cancelBookingForm.setEmail(null);

      when(userService.getAddInfoFromToken()).thenReturn(null);
      when(bookingRepository.findById(cancelBookingForm.getId())).thenReturn(Optional.of(mockBooking1));

      assertThrows(NotFoundException.class, () -> bookingController.cancelBookingByClient(cancelBookingForm, bindingResult));

      verify(bookingRepository, times(1)).findById(cancelBookingForm.getId());
      verify(userService, times(1)).getAddInfoFromToken();
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Test cancel booking failed when the email in the form doesn’t match")
    void testCancelBooking_EmailInFormNotMatch_ShouldFail(){
      CancelBookingForm cancelBookingForm = new CancelBookingForm();
      cancelBookingForm.setId(8L);
      cancelBookingForm.setEmail("TrinhTrungHao2003@gmail.com");

      when(userService.getAddInfoFromToken()).thenReturn(null);
      when(bookingRepository.findById(cancelBookingForm.getId())).thenReturn(Optional.of(mockBooking2));

      assertThrows(UnauthorizationException.class,
          () -> bookingController.cancelBookingByClient(cancelBookingForm, bindingResult));

      verify(bookingRepository, times(1)).findById(cancelBookingForm.getId());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Client cancel booking fail when booking not found")
    void testClientCancelBooking_NotFound_ShouldFail() {
      CancelBookingForm cancelBookingForm = new CancelBookingForm();
      cancelBookingForm.setId(20L);
      cancelBookingForm.setEmail("22110136@student.hcmute.edu.vn");

      when(bookingRepository.findById(cancelBookingForm.getId())).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookingController.cancelBookingByClient(cancelBookingForm, bindingResult));

      verify(bookingRepository, times(1)).findById(cancelBookingForm.getId());
      verify(customerRepository, never()).findById(any());
      verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get list booking success: customer logged in")
    void testGetListBooking_CustomerLoggedIn_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<Booking> bookingList = Collections.singletonList(mockBooking1);
      Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, 1);

      BookingDto bookingDto = new BookingDto();
      bookingDto.setId(5L);
      bookingDto.setDiscount(10.0);
      bookingDto.setTotalPrice(161820.0);
      bookingDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));
      bookingDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingDto.setCustomerInfo(null);

      AccountUserDto accountDto = new AccountUserDto();
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");

      CustomerProfileDto customerDto = new CustomerProfileDto();
      customerDto.setAccount(accountDto);
      bookingDto.setCustomer(customerDto);

      BranchDto branchDto = new BranchDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingDto.setBranch(branchDto);

      List<BookingDto> dtoList = Collections.singletonList(bookingDto);

      when(bookingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookingPage);
      when(bookingMapper.fromEntityToBookingDtoList(bookingList)).thenReturn(dtoList);

      BookingCriteria bookingCriteria = new BookingCriteria();
      bookingCriteria.setCustomerId(customerJwt.getAccountId());
      ApiMessageDto<ResponseListDto<List<BookingDto>>> response = bookingController.listByClient(new BookingCriteria(), pageable);

      ResponseListDto<List<BookingDto>> data = response.getData();

      assertEquals(1, data.getTotalElements());
      assertEquals(1, data.getContent().size());

      BookingDto result = data.getContent().get(0);

      assertEquals(mockBooking1.getId(), result.getId());
      assertEquals(mockBooking1.getBookingDate(), result.getBookingDate());

      verify(bookingRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingMapper, times(1)).fromEntityToBookingDtoList(bookingList);
    }

    @Test
    @DisplayName("Get list booking success: customer not logged in")
    void testGetListBooking_CustomerNotLoggedIn_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<Booking> bookingList = Collections.singletonList(mockBooking2);
      Page<Booking> bookingPage = new PageImpl<>(bookingList, pageable, 1);

      BookingDto bookingDto = new BookingDto();
      bookingDto.setId(8L);
      bookingDto.setDiscount(10.0);
      bookingDto.setTotalPrice(161820.0);
      bookingDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("24/04/2026 10:30"));
      bookingDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);

      BranchDto branchDto = new BranchDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingDto.setBranch(branchDto);
      bookingDto.setCustomer(null);

      BookingCustomerInfoDto customerInfoDto = new BookingCustomerInfoDto();
      customerInfoDto.setEmail("22110316@student.hcmute.edu.vn");
      bookingDto.setCustomerInfo(customerInfoDto);

      List<BookingDto> dtoList = Collections.singletonList(bookingDto);

      when(bookingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookingPage);
      when(bookingMapper.fromEntityToBookingDtoList(bookingList)).thenReturn(dtoList);

      BookingCriteria bookingCriteria = new BookingCriteria();
      bookingCriteria.setEmail("22110316@student.hcmute.edu.vn");
      ApiMessageDto<ResponseListDto<List<BookingDto>>> response = bookingController.listByClient(bookingCriteria, pageable);

      ResponseListDto<List<BookingDto>> data = response.getData();

      assertEquals(1, data.getTotalElements());
      assertEquals(1, data.getContent().size());

      BookingDto result = data.getContent().get(0);

      assertEquals(mockBooking2.getId(), result.getId());
      assertEquals(mockBooking2.getBookingDate(), result.getBookingDate());

      verify(bookingRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingMapper, times(1)).fromEntityToBookingDtoList(bookingList);
    }

    @Test
    @DisplayName("Get detail booking success: customer logged in")
    void testGetDetailBooking_CustomerLoggedIn_ShouldSuccess() throws ParseException {
      BookingDto bookingDto = new BookingDto();
      bookingDto.setId(5L);
      bookingDto.setDiscount(10.0);
      bookingDto.setTotalPrice(161820.0);
      bookingDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));
      bookingDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingDto.setCustomerInfo(null);

      AccountUserDto accountDto = new AccountUserDto();
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");

      CustomerProfileDto customerDto = new CustomerProfileDto();
      customerDto.setAccount(accountDto);
      bookingDto.setCustomer(customerDto);

      BranchDto branchDto = new BranchDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingDto.setBranch(branchDto);


      when(bookingRepository.findById(5L)).thenReturn(Optional.of(mockBooking1));
      when(bookingMapper.fromEntityToBookingDto(mockBooking1)).thenReturn(bookingDto);

      ApiMessageDto<BookingDto> response = bookingController.getByClient(5L);
      BookingDto result = response.getData();

      assertEquals(mockBooking1.getId(), result.getId());
      assertEquals(mockBooking1.getBookingDate(), result.getBookingDate());
      assertEquals(mockBooking1.getTotalPrice(), result.getTotalPrice());

      verify(bookingRepository, times(1)).findById(5L);
      verify(bookingMapper, times(1)).fromEntityToBookingDto(mockBooking1);
    }

    @Test
    @DisplayName("Get detail booking success: customer not logged in")
    void testGetDetailBooking_CustomerNotLoggedIn_ShouldSuccess() throws ParseException {
      BookingDto bookingDto = new BookingDto();
      bookingDto.setId(8L);
      bookingDto.setDiscount(10.0);
      bookingDto.setTotalPrice(70000.0);
      bookingDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("24/04/2026 10:30"));
      bookingDto.setStatus(BarberConstant.BOOKING_STATUS_PENDING);
      bookingDto.setCustomer(null);

      BranchDto branchDto = new BranchDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingDto.setBranch(branchDto);


      BookingCustomerInfoDto customerInfoDto = new BookingCustomerInfoDto();
      customerInfoDto.setEmail("22110316@student.hcmute.edu.vn");
      bookingDto.setCustomerInfo(customerInfoDto);

      when(bookingRepository.findById(8L)).thenReturn(Optional.of(mockBooking2));
      when(bookingMapper.fromEntityToBookingDto(mockBooking2)).thenReturn(bookingDto);

      ApiMessageDto<BookingDto> response = bookingController.getByClient(8L);
      BookingDto result = response.getData();

      assertEquals(mockBooking2.getId(), result.getId());
      assertEquals(mockBooking2.getBookingDate(), result.getBookingDate());

      verify(bookingRepository, times(1)).findById(8L);
      verify(bookingMapper, times(1)).fromEntityToBookingDto(mockBooking2);
    }

    @Test
    @DisplayName("Get detail booking fail when booking not found")
    void testGetDetailBooking_BookingNotFound_ShouldFail() {
      when(bookingRepository.findById(20L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> bookingController.getByClient(20L));

      verify(bookingRepository, times(1)).findById(20L);
      verify(bookingMapper, never()).fromEntityToBookingDto(any());
    }
  }
}
