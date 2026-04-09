package com.barber.api.unit.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.barber.api.constant.BarberConstant;
import com.barber.api.controller.CustomerController;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.OtpDto;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.account.AccountDto;
import com.barber.api.dto.account.AccountUserDto;
import com.barber.api.dto.customer.CustomerDto;
import com.barber.api.dto.customer.CustomerProfileDto;
import com.barber.api.dto.group.GroupDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.form.customer.SignUpCustomerForm;
import com.barber.api.form.customer.UpdateCustomerForm;
import com.barber.api.form.customer.UpdateCustomerProfileForm;
import com.barber.api.jwt.BarberJwt;
import com.barber.api.mapper.AccountMapper;
import com.barber.api.mapper.CustomerMapper;
import com.barber.api.model.Account;
import com.barber.api.model.Customer;
import com.barber.api.model.Group;
import com.barber.api.model.Permission;
import com.barber.api.model.criteria.CustomerCriteria;
import com.barber.api.repository.AccountRepository;
import com.barber.api.repository.CustomerRepository;
import com.barber.api.repository.GroupRepository;
import com.barber.api.service.BarberApiService;
import com.barber.api.service.impl.UserServiceImpl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {
  @InjectMocks
  CustomerController customerController;

  @Mock
  CustomerRepository customerRepository;

  @Mock
  AccountRepository accountRepository;

  @Mock
  GroupRepository groupRepository;

  @Mock
  CustomerMapper customerMapper;

  @Mock
  AccountMapper accountMapper;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  BarberApiService barberApiService;

  @Mock
  UserServiceImpl userService;

  @Mock
  BindingResult bindingResult;

  static Account mockAccount;

  static Customer mockCustomer;

  static Group mockGroup;

  static Permission mockPermission1;

  static Permission mockPermission2;

  Long customerId;

  @BeforeAll
  static void initData() throws ParseException {
    mockPermission1 = new Permission();
    mockPermission1.setId(3L);
    mockPermission1.setAction("/v1/customer/profile");
    mockPermission1.setName("Get profile customer");
    mockPermission1.setDescription("Get profile customer");
    mockPermission1.setNameGroup("Customer");
    mockPermission1.setPCode("CUS_US_V");
    mockPermission1.setShowMenu(false);

    mockPermission2 = new Permission();
    mockPermission2.setId(4L);
    mockPermission2.setAction("/v1/customer/update-profile");
    mockPermission2.setName("Update profile customer");
    mockPermission2.setDescription("Update profile customer");
    mockPermission2.setNameGroup("Customer");
    mockPermission2.setPCode("CUS_US_U");
    mockPermission2.setShowMenu(false);

    mockGroup = new Group();
    mockGroup.setId(2L);
    mockGroup.setName("ROLE CUSTOMER");
    mockGroup.setDescription("Role for customer account");
    mockGroup.setIsSystemRole(false);
    mockGroup.setPermissions(Arrays.asList(mockPermission1, mockPermission2));

    mockAccount = new Account();
    mockAccount.setId(1L);
    mockAccount.setUsername("trunghao");
    mockAccount.setFullName("Trịnh Trung Hào");
    mockAccount.setEmail("trunghao@gmail.com");
    mockAccount.setPhone("0879738264");
    mockAccount.setPassword("{bcrypt}$2a$10$FQampepPuO0qoXtfCnfgOuSYAn4B/MA.Nh1VOg01VpyGL25p9xf7.");
    mockAccount.setKind(BarberConstant.USER_KIND_CUSTOMER);
    mockAccount.setIsSuperAdmin(false);
    mockAccount.setGroup(mockGroup);

    mockCustomer = new Customer();
    mockCustomer.setId(1L);
    mockCustomer.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("01/11/2003"));
    mockCustomer.setAccount(mockAccount);
    mockCustomer.setScore(0L);
  }

  @Nested
  @DisplayName("Admin actions")
  class AdminTest {
    @BeforeEach
    void setUpAdmin(){
      BarberJwt adminJwt = new BarberJwt();
      adminJwt.setUserKind(BarberConstant.USER_KIND_ADMIN);
      when(userService.getAddInfoFromToken()).thenReturn(adminJwt);
    }

    @Test
    @DisplayName("Get list customer success")
    void testGetListCustomer_ValidCriteria_ShouldSuccess() throws ParseException {
      Pageable pageable = PageRequest.of(0, 10);
      List<Customer> customerList = Collections.singletonList(mockCustomer);
      Page<Customer> customerPage = new PageImpl<>(customerList, pageable, 1);

      GroupDto groupDto = new GroupDto();
      groupDto.setId(2L);
      groupDto.setName("ROLE CUSTOMER");
      groupDto.setDescription("Role for customer account");
      groupDto.setPermissions(Arrays.asList(mockPermission1, mockPermission2));

      AccountDto accountDto = new AccountDto();
      accountDto.setId(1L);
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");
      accountDto.setKind(BarberConstant.USER_KIND_CUSTOMER);
      accountDto.setIsSuperAdmin(false);
      accountDto.setGroup(groupDto);

      CustomerDto customerDto = new CustomerDto();
      customerDto.setId(1L);
      customerDto.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("01/11/2003"));
      customerDto.setAccount(accountDto);
      customerDto.setScore(0L);

      when(customerRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(customerPage);
      when(customerMapper.fromEntityToCustomerDtoList(customerList)).thenReturn(List.of(customerDto));

      CustomerCriteria criteria = new CustomerCriteria();
      ApiMessageDto<ResponseListDto<List<CustomerDto>>> response = customerController.list(criteria, pageable);

      assertEquals("trunghao", response.getData().getContent().get(0).getAccount().getUsername());
      assertEquals("Trịnh Trung Hào", response.getData().getContent().get(0).getAccount().getFullName());
      assertEquals("trunghao@gmail.com", response.getData().getContent().get(0).getAccount().getEmail());
      assertEquals("0879738264", response.getData().getContent().get(0).getAccount().getPhone());
      assertEquals(1, response.getData().getTotalElements());
      assertEquals(1, response.getData().getTotalPages());

      verify(customerRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
      verify(customerMapper, times(1)).fromEntityToCustomerDtoList(customerList);
    }

    @Test
    @DisplayName("Get detail customer success")
    void testGetDetail_ExistingId_ShouldSuccess() throws ParseException {
      GroupDto groupDto = new GroupDto();
      groupDto.setId(2L);
      groupDto.setName("ROLE CUSTOMER");
      groupDto.setDescription("Role for customer account");
      groupDto.setPermissions(Arrays.asList(mockPermission1, mockPermission2));

      AccountDto accountDto = new AccountDto();
      accountDto.setId(1L);
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");
      accountDto.setKind(BarberConstant.USER_KIND_CUSTOMER);
      accountDto.setIsSuperAdmin(false);
      accountDto.setGroup(groupDto);

      CustomerDto customerDto = new CustomerDto();
      customerDto.setId(1L);
      customerDto.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("01/11/2003"));
      customerDto.setAccount(accountDto);
      customerDto.setScore(0L);

      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(customerMapper.fromEntityToCustomerDto(mockCustomer)).thenReturn(customerDto);

      ApiMessageDto<CustomerDto> response = customerController.get(1L);

      assertEquals("trunghao", response.getData().getAccount().getUsername());
      assertEquals("Trịnh Trung Hào", response.getData().getAccount().getFullName());
      assertEquals("trunghao@gmail.com", response.getData().getAccount().getEmail());
      assertEquals("0879738264", response.getData().getAccount().getPhone());

      verify(customerRepository, times(1)).findById(1L);
      verify(customerMapper, times(1)).fromEntityToCustomerDto(eq(mockCustomer));
    }

    @Test
    @DisplayName("Get detail customer not found")
    void testGetDetail_NotFound_ShouldFail() {
      when(customerRepository.findById(10L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> customerController.get(10L));

      verify(customerRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Update customer success")
    void tesUpdateCustomer_ShouldSuccess() throws ParseException {
      UpdateCustomerForm updateCustomerForm = new UpdateCustomerForm();
      updateCustomerForm.setId(1L);
      updateCustomerForm.setUsername("phuckhai");
      updateCustomerForm.setFullName("Lại Hoàng Phúc Khải");
      updateCustomerForm.setEmail("phuckhai@gmail.com");
      updateCustomerForm.setPhone("0738296431");
      updateCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/02/2004"));
      updateCustomerForm.setPassword("Khai12345@");

      Account newAccount = new Account();
      newAccount.setId(1L);
      newAccount.setUsername("phuckhai");
      newAccount.setFullName("Lại Hoàng Phúc Khải");
      newAccount.setEmail("phuckhai@gmail.com");
      newAccount.setPhone("0738296431");
      newAccount.setPassword("{bcrypt}$2a$12$dgfIWxSBj3k2VlBCD6aqWu71SL3MNCZ3fAi.o1o6qhELdJZsAO6r.");
      newAccount.setKind(BarberConstant.USER_KIND_CUSTOMER);
      newAccount.setIsSuperAdmin(false);
      newAccount.setGroup(mockGroup);

      Customer newCustomer = new Customer();
      newCustomer.setId(1L);
      newCustomer.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/02/2004"));
      newCustomer.setAccount(mockAccount);
      newCustomer.setScore(0L);

      when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(updateCustomerForm.getUsername())).thenReturn(false);
      when(accountRepository.existsByEmail(updateCustomerForm.getEmail())).thenReturn(false);
      when(accountRepository.existsByPhone(updateCustomerForm.getPhone())).thenReturn(false);
      when(passwordEncoder.encode(eq(updateCustomerForm.getPassword()))).thenReturn("{bcrypt}$2a$12$dgfIWxSBj3k2VlBCD6aqWu71SL3MNCZ3fAi.o1o6qhELdJZsAO6r.");
      doNothing().when(accountMapper).fromUpdateCustomerFormToEntity(updateCustomerForm, newAccount);

      ApiMessageDto<String> result = customerController.update(updateCustomerForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(accountRepository, times(1)).findById(1L);
      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(updateCustomerForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(updateCustomerForm.getPhone()));
      verify(passwordEncoder, times(1)).encode(eq(updateCustomerForm.getPassword()));
      verify(accountMapper, times(1)).fromUpdateCustomerFormToEntity(eq(updateCustomerForm), eq(newAccount));
      verify(accountRepository, times(1)).save(mockAccount);
      verify(customerRepository, times(1)).save(mockCustomer);
    }

    @Test
    @DisplayName("Update customer fails when customer not found")
    void testUpdateCustomer_NotFound_ShouldFail() throws ParseException {
      UpdateCustomerForm updateCustomerForm = new UpdateCustomerForm();
      updateCustomerForm.setId(12L);
      updateCustomerForm.setUsername("phuckhai");
      updateCustomerForm.setFullName("Lại Hoàng Phúc Khải");
      updateCustomerForm.setEmail("phuckhai@gmail.com");
      updateCustomerForm.setPhone("0738296431");
      updateCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/02/2004"));
      updateCustomerForm.setPassword("Khai12345@");

      when(customerRepository.findById(12L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> customerController.update(updateCustomerForm, bindingResult));

      verify(customerRepository, times(1)).findById(12L);
      verify(accountRepository, never()).findById(any());
      verify(accountRepository, never()).existsByUsername(any());
      verify(accountRepository, never()).existsByEmail(any());
      verify(accountRepository, never()).existsByPhone(any());
      verify(accountMapper, never()).fromUpdateCustomerFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update customer fails when username already exist")
    void tesUpdateCustomer_UsernameExist_ShouldFail() throws ParseException {
      UpdateCustomerForm updateCustomerForm = new UpdateCustomerForm();
      updateCustomerForm.setId(1L);
      updateCustomerForm.setUsername("thanhha");
      updateCustomerForm.setFullName("Nguyễn Thị Thanh Hà");
      updateCustomerForm.setEmail("thanhha@gmail.com");
      updateCustomerForm.setPhone("0978637284");
      updateCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2003"));
      updateCustomerForm.setPassword("Ha12345@");

      Account existAccount = new Account();
      existAccount.setId(6L);
      existAccount.setUsername("thanhha");

      Customer existCustomer = new Customer();
      existCustomer.setId(6L);
      existCustomer.setAccount(existAccount);

      when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(eq(updateCustomerForm.getUsername()))).thenReturn(true);

      assertThrows(BadRequestException.class, () -> customerController.update(updateCustomerForm, bindingResult));

      verify(accountRepository, times(1)).findById(1L);
      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerForm.getUsername()));
      verify(accountRepository, never()).existsByEmail(any());
      verify(accountRepository, never()).existsByPhone(any());
      verify(accountMapper, never()).fromUpdateCustomerFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update customer fails when email already exist")
    void tesUpdateCustomer_EmailExist_ShouldFail() throws ParseException {
      UpdateCustomerForm updateCustomerForm = new UpdateCustomerForm();
      updateCustomerForm.setId(1L);
      updateCustomerForm.setUsername("thanhha");
      updateCustomerForm.setFullName("Nguyễn Thị Thanh Hà");
      updateCustomerForm.setEmail("thanhha@gmail.com");
      updateCustomerForm.setPhone("0978637284");
      updateCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2003"));
      updateCustomerForm.setPassword("Ha12345@");

      Account existAccount = new Account();
      existAccount.setId(6L);
      existAccount.setEmail("thanhha@gmail.com");

      Customer existCustomer = new Customer();
      existCustomer.setId(6L);
      existCustomer.setAccount(existAccount);

      when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(eq(updateCustomerForm.getUsername()))).thenReturn(false);
      when(accountRepository.existsByEmail(eq(updateCustomerForm.getEmail()))).thenReturn(true);

      assertThrows(BadRequestException.class, () -> customerController.update(updateCustomerForm, bindingResult));

      verify(accountRepository, times(1)).findById(1L);
      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(updateCustomerForm.getEmail()));
      verify(accountRepository, never()).existsByPhone(any());
      verify(accountMapper, never()).fromUpdateCustomerFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update customer fails when phone already exist")
    void tesUpdateCustomer_PhoneExist_ShouldFail() throws ParseException {
      UpdateCustomerForm updateCustomerForm = new UpdateCustomerForm();
      updateCustomerForm.setId(1L);
      updateCustomerForm.setUsername("thanhha");
      updateCustomerForm.setFullName("Nguyễn Thị Thanh Hà");
      updateCustomerForm.setEmail("thanhha@gmail.com");
      updateCustomerForm.setPhone("0978637284");
      updateCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2003"));
      updateCustomerForm.setPassword("Ha12345@");

      Account existAccount = new Account();
      existAccount.setId(6L);
      existAccount.setPhone("0978637284");

      Customer existCustomer = new Customer();
      existCustomer.setId(6L);
      existCustomer.setAccount(existAccount);

      when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(eq(updateCustomerForm.getUsername()))).thenReturn(false);
      when(accountRepository.existsByEmail(eq(updateCustomerForm.getEmail()))).thenReturn(false);
      when(accountRepository.existsByPhone(eq(updateCustomerForm.getPhone()))).thenReturn(true);

      assertThrows(BadRequestException.class, () -> customerController.update(updateCustomerForm, bindingResult));

      verify(accountRepository, times(1)).findById(1L);
      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(updateCustomerForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(updateCustomerForm.getPhone()));
      verify(accountMapper, never()).fromUpdateCustomerFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete customer success")
    void testDelete_ShouldSuccess() {
      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.findById(1L)).thenReturn(Optional.of(mockAccount));

      ApiMessageDto<String> result = customerController.delete(1L);

      assertEquals(true, result.getResult());
      
      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Delete customer fail when customer not found")
    void testDelete_NotFound_ShouldFail() {
      when(customerRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> customerController.delete(1L));

      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, never()).findById(1L);
    }

    @Test
    @DisplayName("Delete customer fail when account not found")
    void testDelete_AccountNotFound_ShouldFail() {
      when(customerRepository.findById(1L)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> customerController.delete(1L));

      verify(customerRepository, times(1)).findById(1L);
      verify(accountRepository, times(1)).findById(1L);
    }
  }

  @Nested
  @DisplayName("Customer actions")
  class CustomerTest{
    @BeforeEach
    void setupCustomer() {
      customerId = 1L;

      BarberJwt customerJwt = new BarberJwt();
      customerJwt.setAccountId(customerId);
      customerJwt.setUsername("trunghao");
      customerJwt.setUserKind(BarberConstant.USER_KIND_CUSTOMER);

      lenient().when(userService.getAddInfoFromToken()).thenReturn(customerJwt);
    }

    @Test
    @DisplayName("Sign up success")
    void testSignup_ShouldSuccess() throws ParseException {
      SignUpCustomerForm signUpCustomerForm = new SignUpCustomerForm();
      signUpCustomerForm.setUsername("hoangnam");
      signUpCustomerForm.setFullName("Nguyễn Hoàng Nam");
      signUpCustomerForm.setEmail("hoangnam@gmail.com");
      signUpCustomerForm.setPhone("0983627864");
      signUpCustomerForm.setPassword("Nam12345@");
      signUpCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2002"));

      Account newAccount = new Account();
      newAccount.setUsername("hoangnam");
      newAccount.setFullName("Nguyễn Hoàng Nam");
      newAccount.setEmail("hoangnam@gmail.com");
      newAccount.setPhone("0983627864");
      newAccount.setKind(BarberConstant.USER_KIND_CUSTOMER);
      newAccount.setIsSuperAdmin(false);
      newAccount.setGroup(mockGroup);

      Customer newCustomer = new Customer();
      newCustomer.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2002"));
      newCustomer.setAccount(new Account());
      newCustomer.setScore(0L);

      when(accountRepository.existsByUsername(signUpCustomerForm.getUsername())).thenReturn(false);
      when(accountRepository.existsByEmail(signUpCustomerForm.getEmail())).thenReturn(false);
      when(accountRepository.existsByPhone(signUpCustomerForm.getPhone())).thenReturn(false);
      when(groupRepository.findFirstByKind(BarberConstant.USER_KIND_CUSTOMER)).thenReturn(Optional.of(mockGroup));
      when(accountMapper.fromSignUpCustomerFormToEntity(signUpCustomerForm)).thenReturn(newAccount);
      when(customerMapper.fromSignUpCustomerFormToEntity(signUpCustomerForm)).thenReturn(newCustomer);
      when(passwordEncoder.encode(any())).thenReturn("{bcrypt}$2a$12$Q6jIGORL58vguZlu.Cp2uOt/uCL8CGPppLT6LUc4zEJT7QrSGUiAa");
      when(barberApiService.getOTP()).thenReturn("123456");

      when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
        Account account = invocation.getArgument(0);
        account.setId(5L);
        return account;
      });

      when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
        Customer customer = invocation.getArgument(0);
        if (customer.getAccount() != null){
          customer.setId(customer.getAccount().getId());
        }
        return customer;
      });

      ApiMessageDto<OtpDto> response = customerController.signup(signUpCustomerForm, bindingResult);

      assertEquals(true, response.getResult());

      verify(accountRepository, times(1)).existsByUsername(eq(signUpCustomerForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(signUpCustomerForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(signUpCustomerForm.getPhone()));
      verify(groupRepository, times(1)).findFirstByKind(BarberConstant.USER_KIND_CUSTOMER);
      verify(accountMapper, times(1)).fromSignUpCustomerFormToEntity(eq(signUpCustomerForm));
      verify(customerMapper, times(1)).fromSignUpCustomerFormToEntity(eq(signUpCustomerForm));
      verify(barberApiService, times(1)).sendEmail(any(), any(), any(), eq(true));
      verify(accountRepository, times(1)).save(any());
      verify(customerRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Signup fail when username already exist")
    void testSignup_UsernameExist_ShouldFail() throws ParseException {
      SignUpCustomerForm signUpCustomerForm = new SignUpCustomerForm();
      signUpCustomerForm.setUsername("hoangnam");
      signUpCustomerForm.setFullName("Nguyễn Hoàng Nam");
      signUpCustomerForm.setEmail("hoangnam@gmail.com");
      signUpCustomerForm.setPhone("0983627864");
      signUpCustomerForm.setPassword("Nam12345@");
      signUpCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2002"));

      when(accountRepository.existsByUsername(signUpCustomerForm.getUsername())).thenReturn(true);

      assertThrows(BadRequestException.class, () -> customerController.signup(signUpCustomerForm, bindingResult));

      verify(accountRepository, times(1)).existsByUsername(eq(signUpCustomerForm.getUsername()));
      verify(accountRepository, never()).existsByEmail(any());
      verify(accountRepository, never()).existsByPhone(any());
      verify(groupRepository, never()).findFirstByKind(BarberConstant.USER_KIND_CUSTOMER);
      verify(accountMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(customerMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(barberApiService, never()).sendEmail(any(), any(), any(), eq(true));
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Signup fail when email already exist")
    void testSignup_EmailExist_ShouldFail() throws ParseException {
      SignUpCustomerForm signUpCustomerForm = new SignUpCustomerForm();
      signUpCustomerForm.setUsername("hoangnam");
      signUpCustomerForm.setFullName("Nguyễn Hoàng Nam");
      signUpCustomerForm.setEmail("hoangnam@gmail.com");
      signUpCustomerForm.setPhone("0983627864");
      signUpCustomerForm.setPassword("Nam12345@");
      signUpCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2002"));

      when(accountRepository.existsByUsername(signUpCustomerForm.getUsername())).thenReturn(false);
      when(accountRepository.existsByEmail(signUpCustomerForm.getEmail())).thenReturn(true);

      assertThrows(BadRequestException.class, () -> customerController.signup(signUpCustomerForm, bindingResult));

      verify(accountRepository, times(1)).existsByUsername(any());
      verify(accountRepository, times(1)).existsByEmail(eq(signUpCustomerForm.getEmail()));
      verify(accountRepository, never()).existsByPhone(any());
      verify(groupRepository, never()).findFirstByKind(BarberConstant.USER_KIND_CUSTOMER);
      verify(accountMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(customerMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(barberApiService, never()).sendEmail(any(), any(), any(), eq(true));
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Signup fail when phone already exist")
    void testSignup_PhoneExist_ShouldFail() throws ParseException {
      SignUpCustomerForm signUpCustomerForm = new SignUpCustomerForm();
      signUpCustomerForm.setUsername("hoangnam");
      signUpCustomerForm.setFullName("Nguyễn Hoàng Nam");
      signUpCustomerForm.setEmail("hoangnam@gmail.com");
      signUpCustomerForm.setPhone("0983627864");
      signUpCustomerForm.setPassword("Nam12345@");
      signUpCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2002"));

      when(accountRepository.existsByUsername(signUpCustomerForm.getUsername())).thenReturn(false);
      when(accountRepository.existsByEmail(signUpCustomerForm.getEmail())).thenReturn(false);
      when(accountRepository.existsByPhone(signUpCustomerForm.getPhone())).thenReturn(true);

      assertThrows(BadRequestException.class, () -> customerController.signup(signUpCustomerForm, bindingResult));

      verify(accountRepository, times(1)).existsByUsername(eq(signUpCustomerForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(signUpCustomerForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(signUpCustomerForm.getPhone()));
      verify(groupRepository, never()).findFirstByKind(BarberConstant.USER_KIND_CUSTOMER);
      verify(accountMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(customerMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(barberApiService, never()).sendEmail(any(), any(), any(), eq(true));
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Signup fail when group not found")
    void testSignup_GroupNotFound_ShouldFail() throws ParseException {
      SignUpCustomerForm signUpCustomerForm = new SignUpCustomerForm();
      signUpCustomerForm.setUsername("hoangnam");
      signUpCustomerForm.setFullName("Nguyễn Hoàng Nam");
      signUpCustomerForm.setEmail("hoangnam@gmail.com");
      signUpCustomerForm.setPhone("0983627864");
      signUpCustomerForm.setPassword("Nam12345@");
      signUpCustomerForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("24/07/2002"));

      when(accountRepository.existsByUsername(eq(signUpCustomerForm.getUsername()))).thenReturn(false);
      when(accountRepository.existsByEmail(eq(signUpCustomerForm.getEmail()))).thenReturn(false);
      when(accountRepository.existsByPhone(eq(signUpCustomerForm.getPhone()))).thenReturn(false);
      when(groupRepository.findFirstByKind(BarberConstant.USER_KIND_CUSTOMER)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> customerController.signup(signUpCustomerForm, bindingResult));

      verify(accountRepository, times(1)).existsByUsername(eq(signUpCustomerForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(signUpCustomerForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(signUpCustomerForm.getPhone()));
      verify(groupRepository, times(1)).findFirstByKind(BarberConstant.USER_KIND_CUSTOMER);
      verify(accountMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(customerMapper, never()).fromSignUpCustomerFormToEntity(any());
      verify(barberApiService, never()).sendEmail(any(), any(), any(), eq(true));
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get profile customer success")
    void testGetProfile_ShouldSuccess() throws ParseException {
      AccountUserDto accountDto = new AccountUserDto();
      accountDto.setUsername("trunghao");
      accountDto.setFullName("Trịnh Trung Hào");
      accountDto.setEmail("trunghao@gmail.com");
      accountDto.setPhone("0879738264");

      CustomerProfileDto customerDto = new CustomerProfileDto();
      customerDto.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("01/11/2003"));
      customerDto.setScore(0L);
      customerDto.setAccount(accountDto);

      when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
      when(customerMapper.fromEntityToCustomerProfileDto(mockCustomer)).thenReturn(customerDto);

      ApiMessageDto<CustomerProfileDto> response = customerController.profile();

      assertEquals("trunghao", response.getData().getAccount().getUsername());
      assertEquals("Trịnh Trung Hào", response.getData().getAccount().getFullName());
      assertEquals("trunghao@gmail.com", response.getData().getAccount().getEmail());
      assertEquals("0879738264", response.getData().getAccount().getPhone());

      verify(customerRepository, times(1)).findById(customerId);
      verify(customerMapper, times(1)).fromEntityToCustomerProfileDto(eq(mockCustomer));
    }

    @Test
    @DisplayName("Get profile customer fail when customer not found")
    void testGetProfile_NotFound_ShouldFail() {
      when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

      assertThrows(NotFoundException.class, () -> customerController.profile());

      verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Update profile customer success")
    void testUpdateProfile_ShouldSuccess() throws ParseException {
      UpdateCustomerProfileForm updateCustomerProfileForm = new UpdateCustomerProfileForm();
      updateCustomerProfileForm.setUsername("phuckhai");
      updateCustomerProfileForm.setFullName("Lại Hoàng Phúc Khải");
      updateCustomerProfileForm.setEmail("phuckhai@gmail.com");
      updateCustomerProfileForm.setPhone("0738296431");
      updateCustomerProfileForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/02/2004"));

      Account newAccount = new Account();
      newAccount.setId(1L);
      newAccount.setUsername("phuckhai");
      newAccount.setFullName("Lại Hoàng Phúc Khải");
      newAccount.setEmail("phuckhai@gmail.com");
      newAccount.setPhone("0738296431");
      newAccount.setPassword("{bcrypt}$2a$12$dgfIWxSBj3k2VlBCD6aqWu71SL3MNCZ3fAi.o1o6qhELdJZsAO6r.");
      newAccount.setKind(BarberConstant.USER_KIND_CUSTOMER);
      newAccount.setIsSuperAdmin(false);
      newAccount.setGroup(mockGroup);

      Customer newCustomer = new Customer();
      newCustomer.setId(1L);
      newCustomer.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/02/2004"));
      newCustomer.setAccount(mockAccount);
      newCustomer.setScore(0L);

      when(accountRepository.findById(customerId)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(updateCustomerProfileForm.getUsername())).thenReturn(false);
      when(accountRepository.existsByEmail(updateCustomerProfileForm.getEmail())).thenReturn(false);
      when(accountRepository.existsByPhone(updateCustomerProfileForm.getPhone())).thenReturn(false);
      doNothing().when(accountMapper).fromUpdateCustomerProfileFormToEntity(updateCustomerProfileForm, newAccount);

      ApiMessageDto<String> result = customerController.updateProfile(updateCustomerProfileForm, bindingResult);

      assertEquals(true, result.getResult());

      verify(accountRepository, times(1)).findById(customerId);
      verify(customerRepository, times(1)).findById(customerId);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerProfileForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(updateCustomerProfileForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(updateCustomerProfileForm.getPhone()));
      verify(accountMapper, times(1)).fromUpdateCustomerProfileFormToEntity(eq(updateCustomerProfileForm), eq(newAccount));
      verify(accountRepository, times(1)).save(mockAccount);
      verify(customerRepository, times(1)).save(mockCustomer);
    }

    @Test
    @DisplayName("Update profile customer fails when username already exist")
    void testUpdateProfile_UsernameExist_ShouldFail() throws ParseException {
      UpdateCustomerProfileForm updateCustomerProfileForm = new UpdateCustomerProfileForm();
      updateCustomerProfileForm.setUsername("thanhha");
      updateCustomerProfileForm.setFullName("Nguyễn Thị Thanh Hà");
      updateCustomerProfileForm.setEmail("thanhha@gmail.com");
      updateCustomerProfileForm.setPhone("0978637284");
      updateCustomerProfileForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2003"));

      Account existAccount = new Account();
      existAccount.setId(6L);
      existAccount.setUsername("thanhha");

      Customer existCustomer = new Customer();
      existCustomer.setId(6L);
      existCustomer.setAccount(existAccount);


      when(accountRepository.findById(customerId)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(eq(updateCustomerProfileForm.getUsername()))).thenReturn(true);


      assertThrows(BadRequestException.class, () -> customerController.updateProfile(updateCustomerProfileForm, bindingResult));

      verify(accountRepository, times(1)).findById(customerId);
      verify(customerRepository, times(1)).findById(customerId);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerProfileForm.getUsername()));
      verify(accountRepository, never()).existsByEmail(any());
      verify(accountRepository, never()).existsByPhone(any());
      verify(accountMapper, never()).fromUpdateCustomerProfileFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update profile customer fails when email already exist")
    void testUpdateProfile_EmailExist_ShouldFail() throws ParseException {
      UpdateCustomerProfileForm updateCustomerProfileForm = new UpdateCustomerProfileForm();
      updateCustomerProfileForm.setUsername("thanhha");
      updateCustomerProfileForm.setFullName("Nguyễn Thị Thanh Hà");
      updateCustomerProfileForm.setEmail("thanhha@gmail.com");
      updateCustomerProfileForm.setPhone("0978637284");
      updateCustomerProfileForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2003"));

      Account existAccount = new Account();
      existAccount.setId(6L);
      existAccount.setEmail("thanhha@gmail.com");

      Customer existCustomer = new Customer();
      existCustomer.setId(6L);
      existCustomer.setAccount(existAccount);


      when(accountRepository.findById(customerId)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(eq(updateCustomerProfileForm.getUsername()))).thenReturn(false);
      when(accountRepository.existsByEmail(eq(updateCustomerProfileForm.getEmail()))).thenReturn(true);


      assertThrows(BadRequestException.class, () -> customerController.updateProfile(updateCustomerProfileForm, bindingResult));

      verify(accountRepository, times(1)).findById(customerId);
      verify(customerRepository, times(1)).findById(customerId);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerProfileForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(updateCustomerProfileForm.getEmail()));
      verify(accountRepository, never()).existsByPhone(any());
      verify(accountMapper, never()).fromUpdateCustomerProfileFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update profile customer fails when phone already exist")
    void testUpdateProfile_PhoneExist_ShouldFail() throws ParseException {
      UpdateCustomerProfileForm updateCustomerProfileForm = new UpdateCustomerProfileForm();
      updateCustomerProfileForm.setUsername("thanhha");
      updateCustomerProfileForm.setFullName("Nguyễn Thị Thanh Hà");
      updateCustomerProfileForm.setEmail("thanhha@gmail.com");
      updateCustomerProfileForm.setPhone("0978637284");
      updateCustomerProfileForm.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse("12/03/2003"));

      Account existAccount = new Account();
      existAccount.setId(6L);
      existAccount.setPhone("0978637284");

      Customer existCustomer = new Customer();
      existCustomer.setId(6L);
      existCustomer.setAccount(existAccount);


      when(accountRepository.findById(customerId)).thenReturn(Optional.of(mockAccount));
      when(customerRepository.findById(customerId)).thenReturn(Optional.of(mockCustomer));
      when(accountRepository.existsByUsername(eq(updateCustomerProfileForm.getUsername()))).thenReturn(false);
      when(accountRepository.existsByEmail(eq(updateCustomerProfileForm.getEmail()))).thenReturn(false);
      when(accountRepository.existsByPhone(eq(updateCustomerProfileForm.getPhone()))).thenReturn(true);


      assertThrows(BadRequestException.class, () -> customerController.updateProfile(updateCustomerProfileForm, bindingResult));

      verify(accountRepository, times(1)).findById(customerId);
      verify(customerRepository, times(1)).findById(customerId);
      verify(accountRepository, times(1)).existsByUsername(eq(updateCustomerProfileForm.getUsername()));
      verify(accountRepository, times(1)).existsByEmail(eq(updateCustomerProfileForm.getEmail()));
      verify(accountRepository, times(1)).existsByPhone(eq(updateCustomerProfileForm.getPhone()));
      verify(accountMapper, never()).fromUpdateCustomerProfileFormToEntity(any(), any());
      verify(accountRepository, never()).save(any());
      verify(customerRepository, never()).save(any());
    }
  }
}
