package com.barber.api.controller;

import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.OtpDto;
import com.barber.api.dto.ResponseListDto;
import com.barber.api.dto.customer.CustomerDto;
import com.barber.api.dto.customer.CustomerProfileDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.exception.UnauthorizationException;
import com.barber.api.form.customer.SignUpCustomerForm;
import com.barber.api.form.customer.UpdateCustomerForm;
import com.barber.api.form.customer.UpdateCustomerProfileForm;
import com.barber.api.mapper.AccountMapper;
import com.barber.api.mapper.CustomerMapper;
import com.barber.api.model.Account;
import com.barber.api.model.Customer;
import com.barber.api.model.Group;
import com.barber.api.model.criteria.CustomerCriteria;
import com.barber.api.repository.AccountRepository;
import com.barber.api.repository.BookingRepository;
import com.barber.api.repository.BookingServiceRepository;
import com.barber.api.repository.CustomerRepository;
import com.barber.api.repository.GroupRepository;
import com.barber.api.service.BarberApiService;
import com.barber.api.utils.AESUtils;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/v1/customer")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class CustomerController extends ABasicController{
  @Autowired
  CustomerRepository customerRepository;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  GroupRepository groupRepository;

  @Autowired
  BookingRepository bookingRepository;

  @Autowired
  CustomerMapper customerMapper;

  @Autowired
  AccountMapper accountMapper;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  BarberApiService barberApiService;

  @PostMapping(value = "/signup", produces = MediaType.APPLICATION_JSON_VALUE)
  public ApiMessageDto<OtpDto> signup(@Valid @RequestBody SignUpCustomerForm signUpCustomerForm, BindingResult bindingResult){
    ApiMessageDto<OtpDto> apiMessageDto = new ApiMessageDto<>();
    Boolean existUsername = accountRepository.existsByUsername(signUpCustomerForm.getUsername());
    if (existUsername){
      throw new BadRequestException("Account username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
    }

    Boolean existEmail = accountRepository.existsByEmail(signUpCustomerForm.getEmail());
    if (existEmail){
      throw new BadRequestException("Account email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
    }

    Boolean existPhone = accountRepository.existsByPhone(signUpCustomerForm.getPhone());
    if (existPhone){
      throw new BadRequestException("Account phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
    }

    Group group = groupRepository.findFirstByKind(BarberConstant.USER_KIND_CUSTOMER)
        .orElseThrow(() -> new NotFoundException("Group not found", ErrorCode.GROUP_ERROR_NOT_FOUND));


    Account account = accountMapper.fromSignUpCustomerFormToEntity(signUpCustomerForm);
    account.setPassword(passwordEncoder.encode(signUpCustomerForm.getPassword()));
    account.setKind(BarberConstant.USER_KIND_CUSTOMER);
    account.setGroup(group);
    account.setStatus(BarberConstant.STATUS_PENDING);
    accountRepository.save(account);

    Customer customer = customerMapper.fromSignUpCustomerFormToEntity(signUpCustomerForm);
    customer.setAccount(account);

    String otp = barberApiService.getOTP();
    customer.setOtp(otp);
    customer.setOtpFailCount(0);
    customer.setOtpCreatedAt(new Date());
    customerRepository.save(customer);

    sendVerifyOtp(account, customer);
    OtpDto otpDto = new OtpDto();
    String hash = AESUtils.encrypt(account.getId() + ";" + otp, true);
    otpDto.setIdHash(hash);
    apiMessageDto.setData(otpDto);
    apiMessageDto.setMessage("Signup success");
    return apiMessageDto;
  }

  @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CUS_L')")
  public ApiMessageDto<ResponseListDto<List<CustomerDto>>> list(CustomerCriteria customerCriteria, Pageable pageable){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<ResponseListDto<List<CustomerDto>>> apiMessageDto = new ApiMessageDto<>();
    ResponseListDto<List<CustomerDto>> responseListDto = new ResponseListDto<>();
    Page<Customer> customers = customerRepository.findAll(customerCriteria.getSpecification(), pageable);
    List<CustomerDto> customerDtos = customerMapper.fromEntityToCustomerDtoList(customers.getContent());
    responseListDto.setContent(customerDtos);
    responseListDto.setTotalElements(customers.getTotalElements());
    responseListDto.setTotalPages(customers.getTotalPages());
    apiMessageDto.setData(responseListDto);
    apiMessageDto.setMessage("Get list customer success");
    return apiMessageDto;
  }

  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CUS_V')")
  public ApiMessageDto<CustomerDto> get(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<CustomerDto> apiMessageDto = new ApiMessageDto<>();
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
    CustomerDto customerDto = customerMapper.fromEntityToCustomerDto(customer);
    apiMessageDto.setData(customerDto);
    apiMessageDto.setMessage("Get detail customer success");
    return apiMessageDto;
  }

  @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CUS_US_V')")
  public ApiMessageDto<CustomerProfileDto> profile(){
    ApiMessageDto<CustomerProfileDto> apiMessageDto = new ApiMessageDto<>();
    Customer customer = customerRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
    CustomerProfileDto customerProfileDto = customerMapper.fromEntityToCustomerProfileDto(customer);
    apiMessageDto.setData(customerProfileDto);
    apiMessageDto.setMessage("Get profile customer success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CUS_U')")
  public ApiMessageDto<String> update(@Valid @RequestBody UpdateCustomerForm updateCustomerForm, BindingResult bindingResult){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Customer customer = customerRepository.findById(updateCustomerForm.getId())
        .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
    Account account = accountRepository.findById(updateCustomerForm.getId())
        .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

    if (!updateCustomerForm.getUsername().equals(account.getUsername())){
      Boolean existUsername = accountRepository.existsByUsername(updateCustomerForm.getUsername());
      if (existUsername){
        throw new BadRequestException("Account username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }

    if (!updateCustomerForm.getEmail().equals(account.getEmail())){
      Boolean existEmail = accountRepository.existsByEmail(updateCustomerForm.getEmail());
      if (existEmail){
        throw new BadRequestException("Account email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }

    if (!updateCustomerForm.getPhone().equals(account.getPhone())){
      Boolean existPhone = accountRepository.existsByPhone(updateCustomerForm.getPhone());
      if (existPhone){
        throw new BadRequestException("Account phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }

    if (StringUtils.isNotEmpty(updateCustomerForm.getAvatarPath())){
      barberApiService.deleteFile(updateCustomerForm.getAvatarPath());
      account.setAvatarPath(updateCustomerForm.getAvatarPath());
    }

    if (StringUtils.isNotBlank(updateCustomerForm.getPassword())){
      account.setPassword(passwordEncoder.encode(updateCustomerForm.getPassword()));
    }
    accountMapper.fromUpdateCustomerFormToEntity(updateCustomerForm, account);
    accountRepository.save(account);

    if (updateCustomerForm.getBirthday() != null){
      customer.setBirthday(updateCustomerForm.getBirthday());
    }
    customerRepository.save(customer);
    apiMessageDto.setMessage("Update customer success");
    return apiMessageDto;
  }

  @PutMapping(value = "/update-profile", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CUS_US_U')")
  public ApiMessageDto<String> updateProfile(@Valid @RequestBody UpdateCustomerProfileForm updateCustomerProfileForm, BindingResult bindingResult){
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Account account = accountRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Customer customer = customerRepository.findById(getCurrentUser())
        .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));

    if (!updateCustomerProfileForm.getUsername().equals(account.getUsername())){
      Boolean existUsername = accountRepository.existsByUsername(updateCustomerProfileForm.getUsername());
      if (existUsername){
        throw new BadRequestException("Account username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }

    if (!updateCustomerProfileForm.getEmail().equals(account.getEmail())){
      Boolean existEmail = accountRepository.existsByEmail(updateCustomerProfileForm.getEmail());
      if (existEmail){
        throw new BadRequestException("Account email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }

    if (!updateCustomerProfileForm.getPhone().equals(account.getPhone())){
      Boolean existPhone = accountRepository.existsByPhone(updateCustomerProfileForm.getPhone());
      if (existPhone){
        throw new BadRequestException("Account phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
      }
    }

    if (StringUtils.isNotEmpty(updateCustomerProfileForm.getAvatarPath())){
      barberApiService.deleteFile(updateCustomerProfileForm.getAvatarPath());
      account.setAvatarPath(updateCustomerProfileForm.getAvatarPath());
    }

    accountMapper.fromUpdateCustomerProfileFormToEntity(updateCustomerProfileForm, account);
    accountRepository.save(account);

    if (updateCustomerProfileForm.getBirthday() != null){
      customer.setBirthday(updateCustomerProfileForm.getBirthday());
    }
    customerRepository.save(customer);
    apiMessageDto.setMessage("Update customer success");
    return apiMessageDto;
  }

  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasRole('CUS_D')")
  public ApiMessageDto<String> delete(@PathVariable("id") Long id){
    if (!isAdmin()){
      throw new UnauthorizationException("User is not an admin");
    }
    ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));
    Account account = accountRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
    Boolean existBooking = bookingRepository.existsByCustomerId(id);
    if (existBooking){
      account.setStatus(BarberConstant.STATUS_DELETE);
      accountRepository.save(account);
    } else {
      if (StringUtils.isNotEmpty(account.getAvatarPath())){
        barberApiService.deleteFile(account.getAvatarPath());
      }
      customerRepository.delete(customer);
      accountRepository.delete(account);
    }
    apiMessageDto.setMessage("Delete customer success");
    return apiMessageDto;
  }

  private void sendVerifyOtp(Account account, Customer customer){
    String subject = "Xác thực tài khoản";
    String html = "<div style=\"font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; background-color: #f9f9f9;\">" +
        "<h2 style=\"color: #2c3e50; text-align: center;\">Chào bạn!</h2>" +
        "<p style=\"font-size: 16px; line-height: 1.6;\">Bạn đã đăng ký tài khoản thành công. Mã OTP của bạn là:</p>" +
        "<p style=\"font-size: 18px; font-weight: bold; text-align: center; color: #e74c3c; background-color: #fff; border: 1px dashed #e74c3c; padding: 10px; border-radius: 4px;\">" + customer.getOtp() + "</p>" +
        "<p style=\"font-size: 16px; line-height: 1.6;\">Vui lòng nhập mã này vào trang xác thực để kích hoạt tài khoản.</p>" +
        "<p style=\"font-size: 16px; line-height: 1.6; color: #555;\">Mã OTP có hiệu lực trong <strong>5 phút</strong>.</p>" +
        "<p style=\"font-size: 14px; color: #999; font-style: italic;\">Nếu bạn không thực hiện hành động này, hãy bỏ qua email này.</p>" +
        "</div>";
    barberApiService.sendEmail(account.getEmail(), html, subject, true);
  }
}
