package com.barber.api.unit.bookingService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barber.api.constant.BarberConstant;
import com.barber.api.controller.BookingServiceController;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.account.AccountDto;
import com.barber.api.dto.account.AccountUserDto;
import com.barber.api.dto.booking.BookingAdminDto;
import com.barber.api.dto.booking.BookingCustomerInfoDto;
import com.barber.api.dto.booking.BookingDto;
import com.barber.api.dto.bookingService.BookingServiceAdminDto;
import com.barber.api.dto.bookingService.BookingServiceDto;
import com.barber.api.dto.bookingService.ServiceInfoDto;
import com.barber.api.dto.branch.BranchAdminDto;
import com.barber.api.dto.branch.BranchDto;
import com.barber.api.dto.customer.CustomerDto;
import com.barber.api.dto.customer.CustomerProfileDto;
import com.barber.api.jwt.BarberJwt;
import com.barber.api.mapper.BookingServiceMapper;
import com.barber.api.model.Account;
import com.barber.api.model.Booking;
import com.barber.api.model.BookingService;
import com.barber.api.model.Branch;
import com.barber.api.model.Customer;
import com.barber.api.model.Service;
import com.barber.api.model.criteria.BookingCriteria;
import com.barber.api.model.criteria.BookingServiceCriteria;
import com.barber.api.repository.BookingServiceRepository;
import com.barber.api.service.impl.UserServiceImpl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
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

@ExtendWith(MockitoExtension.class)
public class BookingServiceControllerTest {
  @InjectMocks
  BookingServiceController bookingServiceController;

  @Mock
  BookingServiceRepository bookingServiceRepository;

  @Mock
  BookingServiceMapper bookingServiceMapper;

  @Mock
  UserServiceImpl userService;

  static Customer mockCustomer;

  static Branch mockBranch;

  static Service mockService1;

  static Service mockService2;

  static Booking mockBooking1;

  static Booking mockBooking2;

  static BookingService mockBookingService1;

  static BookingService mockBookingService2;

  static BookingService mockBookingService3;

  static BookingService mockBookingService4;

  @BeforeAll
  static void initData() throws ParseException {
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
    @DisplayName("Get list booking service success")
    void testGetListBookingService_ValidCriteria_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<BookingService> bookingServiceList = Collections.singletonList(mockBookingService1);
      Page<BookingService> bookingServicePage = new PageImpl<>(bookingServiceList, pageable, 1);

      BookingServiceAdminDto bookingServiceAdminDto = new BookingServiceAdminDto();
      bookingServiceAdminDto.setId(6L);
      bookingServiceAdminDto.setPrice(109800.0);
      bookingServiceAdminDto.setServiceId(3L);
      ServiceInfoDto serviceInfoDto = new ServiceInfoDto();
      serviceInfoDto.setName("Cắt gội");
      serviceInfoDto.setPrice(122000.0);
      serviceInfoDto.setSaleOff(10.0);
      bookingServiceAdminDto.setServiceInfo(serviceInfoDto);

      BookingAdminDto bookingAdminDto = new BookingAdminDto();
      bookingAdminDto.setId(5L);
      bookingAdminDto.setDiscount(10.0);
      bookingAdminDto.setTotalPrice(161820.0);
      bookingAdminDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));
      bookingAdminDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingAdminDto.setCustomerInfo(null);
      bookingServiceAdminDto.setBooking(bookingAdminDto);

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

      when(bookingServiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookingServicePage);
      List<BookingServiceAdminDto> dtoList = Collections.singletonList(bookingServiceAdminDto);
      when(bookingServiceMapper.fromEntityToBookingServiceAdminDtoList(bookingServiceList)).thenReturn(dtoList);

      ApiMessageDto<ResponseListDto<List<BookingServiceAdminDto>>> response = bookingServiceController.listByAdmin(new BookingServiceCriteria(), pageable);
      ResponseListDto<List<BookingServiceAdminDto>> data = response.getData();
      assertEquals(1, data.getTotalElements());
      assertEquals(1, data.getTotalPages());

      List<BookingServiceAdminDto> content = data.getContent();
      BookingServiceAdminDto responseDto = content.get(0);
      assertEquals(mockBookingService1.getId(), responseDto.getId());
      assertEquals(mockBookingService1.getServiceId(), responseDto.getServiceId());
      assertEquals(mockBookingService1.getBooking().getId(), responseDto.getBooking().getId());

      verify(bookingServiceRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingServiceMapper, times(1)).fromEntityToBookingServiceAdminDtoList(bookingServiceList);
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
    @DisplayName("Get list booking service success: customer logged in")
    void testGetListBookingService_CustomerLoggedIn_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<BookingService> bookingServiceList = Collections.singletonList(mockBookingService1);
      Page<BookingService> bookingServicePage = new PageImpl<>(bookingServiceList, pageable, 1);

      BookingServiceDto bookingServiceDto = new BookingServiceDto();
      bookingServiceDto.setId(6L);
      bookingServiceDto.setPrice(109800.0);
      bookingServiceDto.setServiceId(3L);
      ServiceInfoDto serviceInfoDto = new ServiceInfoDto();
      serviceInfoDto.setName("Cắt gội");
      serviceInfoDto.setPrice(122000.0);
      serviceInfoDto.setSaleOff(10.0);
      bookingServiceDto.setServiceInfo(serviceInfoDto);

      BookingDto bookingDto = new BookingDto();
      bookingDto.setId(5L);
      bookingDto.setDiscount(10.0);
      bookingDto.setTotalPrice(161820.0);
      bookingDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("23/04/2026 14:30"));
      bookingDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingDto.setCustomerInfo(null);
      bookingServiceDto.setBooking(bookingDto);

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

      when(bookingServiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookingServicePage);
      List<BookingServiceDto> dtoList = Collections.singletonList(bookingServiceDto);
      when(bookingServiceMapper.fromEntityToBookingServiceDtoList(bookingServiceList)).thenReturn(dtoList);

      BookingServiceCriteria bookingServiceCriteria = new BookingServiceCriteria();
      bookingServiceCriteria.setCustomerId(customerJwt.getAccountId());
      bookingServiceCriteria.setBookingId(5L);
      ApiMessageDto<ResponseListDto<List<BookingServiceDto>>> response = bookingServiceController.listByClient(bookingServiceCriteria, pageable);
      ResponseListDto<List<BookingServiceDto>> data = response.getData();
      assertEquals(1, data.getTotalElements());
      assertEquals(1, data.getTotalPages());

      List<BookingServiceDto> content = data.getContent();
      BookingServiceDto responseDto = content.get(0);
      assertEquals(mockBookingService1.getId(), responseDto.getId());
      assertEquals(mockBookingService1.getServiceId(), responseDto.getServiceId());
      assertEquals(mockBookingService1.getBooking().getId(), responseDto.getBooking().getId());

      verify(bookingServiceRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingServiceMapper, times(1)).fromEntityToBookingServiceDtoList(bookingServiceList);
    }

    @Test
    @DisplayName("Get list booking service success: customer not logged in")
    void testGetListBookingService_CustomerNotLoggedIn_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<BookingService> bookingServiceList = Collections.singletonList(mockBookingService3);
      Page<BookingService> bookingServicePage = new PageImpl<>(bookingServiceList, pageable, 1);

      BookingServiceDto bookingServiceDto = new BookingServiceDto();
      bookingServiceDto.setId(9L);
      bookingServiceDto.setPrice(109800.0);
      bookingServiceDto.setServiceId(3L);
      ServiceInfoDto serviceInfoDto = new ServiceInfoDto();
      serviceInfoDto.setName("Cắt gội");
      serviceInfoDto.setPrice(122000.0);
      serviceInfoDto.setSaleOff(10.0);
      bookingServiceDto.setServiceInfo(serviceInfoDto);

      BookingDto bookingDto = new BookingDto();
      bookingDto.setId(8L);
      bookingDto.setDiscount(10.0);
      bookingDto.setTotalPrice(161820.0);
      bookingDto.setBookingDate(new SimpleDateFormat("dd/MM/yyyy HH:mm").parse("24/04/2026 10:30"));
      bookingDto.setStatus(BarberConstant.BOOKING_STATUS_BOOKING);
      bookingDto.setCustomer(null);

      BookingCustomerInfoDto customerInfoDto = new BookingCustomerInfoDto();
      customerInfoDto.setEmail("22110316@student.hcmute.edu.vn");
      bookingDto.setCustomerInfo(customerInfoDto);
      bookingServiceDto.setBooking(bookingDto);

      BranchDto branchDto = new BranchDto();
      branchDto.setId(2L);
      branchDto.setName("Barber - 123 Nguyễn Đình Chiểu");
      bookingDto.setBranch(branchDto);

      when(bookingServiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(bookingServicePage);
      List<BookingServiceDto> dtoList = Collections.singletonList(bookingServiceDto);
      when(bookingServiceMapper.fromEntityToBookingServiceDtoList(bookingServiceList)).thenReturn(dtoList);

      BookingServiceCriteria bookingServiceCriteria = new BookingServiceCriteria();
      bookingServiceCriteria.setEmail("22110316@student.hcmute.edu.vn");
      bookingServiceCriteria.setBookingId(8L);
      ApiMessageDto<ResponseListDto<List<BookingServiceDto>>> response = bookingServiceController.listByClient(bookingServiceCriteria, pageable);
      ResponseListDto<List<BookingServiceDto>> data = response.getData();
      assertEquals(1, data.getTotalElements());
      assertEquals(1, data.getTotalPages());

      List<BookingServiceDto> content = data.getContent();
      BookingServiceDto responseDto = content.get(0);
      assertEquals(mockBookingService3.getId(), responseDto.getId());
      assertEquals(mockBookingService3.getServiceId(), responseDto.getServiceId());
      assertEquals(mockBookingService3.getBooking().getId(), responseDto.getBooking().getId());

      verify(bookingServiceRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(bookingServiceMapper, times(1)).fromEntityToBookingServiceDtoList(bookingServiceList);
    }
  }
}
