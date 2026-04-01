package com.barber.api.controller;


import com.barber.api.constant.BarberConstant;
import com.barber.api.dto.OtpDto;
import com.barber.api.dto.account.AccountAdminDto;
import com.barber.api.dto.account.AccountDto;
import com.barber.api.dto.account.ForgetPasswordDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.exception.NotFoundException;
import com.barber.api.form.account.ForgetPasswordForm;
import com.barber.api.form.account.RequestForgetPasswordForm;
import com.barber.api.form.account.CreateAccountAdminForm;
import com.barber.api.form.account.ResendAccountForm;
import com.barber.api.form.account.UpdateAccountAdminForm;
import com.barber.api.form.account.UpdateProfileAdminForm;
import com.barber.api.form.account.VerifyAccountForm;
import com.barber.api.mapper.AccountMapper;
import com.barber.api.model.Account;
import com.barber.api.model.Customer;
import com.barber.api.model.Group;
import com.barber.api.model.criteria.AccountCriteria;
import com.barber.api.repository.AccountRepository;
import com.barber.api.repository.CustomerRepository;
import com.barber.api.repository.GroupRepository;
import com.barber.api.service.BarberApiService;
import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.utils.AESUtils;
import com.barber.api.utils.ConvertUtils;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/account")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class AccountController extends ABasicController{
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    BarberApiService barberApiService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_C')")
    public ApiMessageDto<String> createAdmin(@Valid @RequestBody CreateAccountAdminForm createAccountAdminForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Boolean existUsername = accountRepository.existsByUsername(createAccountAdminForm.getUsername());
        if (existUsername) {
            throw new BadRequestException("Account username already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
        }

        Boolean existEmail = accountRepository.existsByEmail(createAccountAdminForm.getEmail());
        if (existEmail){
            throw new BadRequestException("Account email already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
        }

        Boolean existPhone = accountRepository.existsByPhone(createAccountAdminForm.getPhone());
        if (existPhone){
            throw new BadRequestException("Account phone already exist", ErrorCode.ACCOUNT_ERROR_EXIST);
        }

        Group group = groupRepository.findById(createAccountAdminForm.getGroupId())
            .orElseThrow(() -> new NotFoundException("Group not found", ErrorCode.ACCOUNT_ERROR_UNKNOWN));

        Account account = accountMapper.fromCreateAdminAccountFormToEntity(createAccountAdminForm);
        account.setPassword(passwordEncoder.encode(createAccountAdminForm.getPassword()));
        account.setKind(BarberConstant.USER_KIND_ADMIN);
        account.setGroup(group);
        accountRepository.save(account);

        apiMessageDto.setMessage("Create account admin success");
        return apiMessageDto;

    }

    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_U')")
    public ApiMessageDto<String> updateAdmin(@Valid @RequestBody UpdateAccountAdminForm updateAccountAdminForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findById(updateAccountAdminForm.getId())
            .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

        Group group = groupRepository.findById(updateAccountAdminForm.getGroupId())
            .orElseThrow(() -> new NotFoundException("Group not found", ErrorCode.ACCOUNT_ERROR_UNKNOWN));
        if (StringUtils.isNoneBlank(updateAccountAdminForm.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateAccountAdminForm.getPassword()));
        }


        if (StringUtils.isNoneBlank(updateAccountAdminForm.getAvatarPath())) {
            if(!updateAccountAdminForm.getAvatarPath().equals(account.getAvatarPath())){
                //delete old image
                barberApiService.deleteFile(account.getAvatarPath());
            }
            account.setAvatarPath(updateAccountAdminForm.getAvatarPath());
        }
        accountMapper.fromUpdateAccountAdminFormToEntity(updateAccountAdminForm, account);
        account.setGroup(group);
        accountRepository.save(account);

        apiMessageDto.setMessage("Update account admin success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_V')")
    public ApiMessageDto<AccountAdminDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<AccountAdminDto> apiMessageDto = new ApiMessageDto<>();

        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

        apiMessageDto.setData(accountMapper.fromAccountToAdminDto(account));
        apiMessageDto.setMessage("Get account successfully");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
            return apiMessageDto;
        }
        if (account.getIsSuperAdmin()){
            throw new BadRequestException("Not allow delete super admin", ErrorCode.ACCOUNT_ERROR_NOT_ALLOW_DELETE_SUPPER_ADMIN);
        }
        //delete avatar file
        barberApiService.deleteFile(account.getAvatarPath());
        accountRepository.deleteById(id);
        apiMessageDto.setMessage("Delete Account success");
        return apiMessageDto;
    }

    @GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_P_AD')")
    public ApiMessageDto<AccountDto> profile() {
        ApiMessageDto<AccountDto> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findById(getCurrentUser())
            .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));
        apiMessageDto.setData(accountMapper.fromAccountToDto(account));
        apiMessageDto.setMessage("Get Account success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update-profile-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_UP_AD')")
    public ApiMessageDto<String> updateProfileAdmin(@Valid @RequestBody UpdateProfileAdminForm updateProfileAdminForm, BindingResult bindingResult) {

        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findById(getCurrentUser())
            .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

        if(!passwordEncoder.matches(updateProfileAdminForm.getOldPassword(), account.getPassword())){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_WRONG_PASSWORD);
            return apiMessageDto;
        }

        if (StringUtils.isNoneBlank(updateProfileAdminForm.getPassword())) {
            account.setPassword(passwordEncoder.encode(updateProfileAdminForm.getPassword()));
        }
        account.setPhone(updateProfileAdminForm.getPhone());
        account.setFullName(updateProfileAdminForm.getFullName());
        account.setAvatarPath(updateProfileAdminForm.getAvatarPath());
        if (updateProfileAdminForm.getGroupId() != null &&
                !updateProfileAdminForm.getGroupId().equals(account.getGroup().getId())) {
            Group group = groupRepository.findById(updateProfileAdminForm.getGroupId()).orElse(null);
            if (group == null) {
                apiMessageDto.setResult(false);
                apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_UNKNOWN);
                return apiMessageDto;
            }
            account.setGroup(group);
        }
        accountRepository.save(account);

        apiMessageDto.setMessage("Update admin account success");
        return apiMessageDto;

    }

    @GetMapping(value = "/list-admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ACC_L_AD')")
    public ApiMessageDto<List<AccountAdminDto>> getListAdmin(AccountCriteria accountCriteria, Pageable pageable) {
        ApiMessageDto<List<AccountAdminDto>> apiMessageDto = new ApiMessageDto<>();
        accountCriteria.setKind(BarberConstant.USER_KIND_ADMIN);
        Specification<Account> specification = accountCriteria.getSpecification();
        Page<Account> pageData = accountRepository.findAll(specification, pageable);
        apiMessageDto.setData(accountMapper.convertAccountToAdminDto(pageData.getContent()));
        apiMessageDto.setMessage("Get list admin success");
        return apiMessageDto;
    }

    @PutMapping(value = "/verify", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> verify(@Valid @RequestBody VerifyAccountForm verifyAccountForm, BindingResult bindingResult){
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        String[] hash = AESUtils.decrypt(verifyAccountForm.getIdHash(),true).split(";",2);
        Long id = ConvertUtils.convertStringToLong(hash[0]);
        if(id <= 0){
            throw new BadRequestException("Incorrect hash verification", ErrorCode.ACCOUNT_ERROR_INCORRECT_HASH_VERIFICATION);
        }

        Account account = accountRepository.findById(id).orElseThrow(()
            -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

        Customer customer = customerRepository.findById(id).orElseThrow(()
            -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));

        if (!Objects.equals(BarberConstant.STATUS_PENDING, account.getStatus())){
            throw new BadRequestException("Account cannot be verified", ErrorCode.ACCOUNT_ERROR_VERIFY_FAILED);
        }

        if(customer.getOtpFailCount() >= BarberConstant.MAX_OTP_INVALID_COUNT){
            account.setStatus(BarberConstant.STATUS_LOCK);
            accountRepository.save(account);
            throw new BadRequestException("Account has been locked", ErrorCode.ACCOUNT_ERROR_LOCKED);
        }

        if(!customer.getOtp().equals(verifyAccountForm.getOtp()) ||
            (new Date().getTime() - customer.getOtpCreatedAt().getTime() >= BarberConstant.MAX_TIME_OTP_CREATED)){
            customer.setOtpFailCount(customer.getOtpFailCount() + 1);
            customerRepository.save(customer);
            throw new BadRequestException("OTP invalid", ErrorCode.ACCOUNT_ERROR_OPT_INVALID);
        }

        account.setStatus(BarberConstant.STATUS_ACTIVE);
        accountRepository.save(account);

        customer.setOtp(null);
        customer.setOtpFailCount(0);
        customer.setOtpCreatedAt(null);
        customerRepository.save(customer);

        apiMessageDto.setMessage("Verify account success");
        return apiMessageDto;
    }

    @PostMapping(value = "/resend", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<OtpDto> resendVerification(@Valid @RequestBody ResendAccountForm resendAccountForm, BindingResult bindingResult){
        ApiMessageDto<OtpDto> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findAccountByEmail(resendAccountForm.getEmail());
        if (account == null) {
            throw new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }

        if (!Objects.equals(account.getStatus(), BarberConstant.STATUS_PENDING)){
            throw new BadRequestException("Account is not pending", ErrorCode.ACCOUNT_ERROR_NOT_PENDING);
        }

        Customer customer = customerRepository.findById(account.getId())
            .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));

        String otp = barberApiService.getOTP();
        customer.setOtpFailCount(0);
        customer.setOtp(otp);
        customer.setOtpCreatedAt(new Date());
        customerRepository.save(customer);

        sendReSendVerifyAccount(account, customer);

        OtpDto otpDto = new OtpDto();
        String hash = AESUtils.encrypt (account.getId()+";"+otp, true);
        otpDto.setIdHash(hash);
        apiMessageDto.setData(otpDto);
        apiMessageDto.setMessage("Send resend verify email success, please check email");
        return apiMessageDto;
    }

    @PostMapping(value = "/request-forget-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<OtpDto> requestForgetPassword(@Valid @RequestBody RequestForgetPasswordForm forgetForm, BindingResult bindingResult){
        ApiMessageDto<OtpDto> apiMessageDto = new ApiMessageDto<>();
        Account account = accountRepository.findAccountByEmail(forgetForm.getEmail());
        if (account == null) {
            throw new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }

        Customer customer = customerRepository.findById(account.getId())
            .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));

        account.setStatus(BarberConstant.STATUS_PENDING);
        accountRepository.save(account);

        String otp = barberApiService.getOTP();
        customer.setOtpFailCount(0);
        customer.setOtp(otp);
        customer.setOtpCreatedAt(new Date());
        customerRepository.save(customer);

        sendForgetPassword(account, customer);

        OtpDto otpDto = new OtpDto();
        String hash = AESUtils.encrypt (account.getId() + ";" + otp, true);
        otpDto.setIdHash(hash);

        apiMessageDto.setData(otpDto);
        apiMessageDto.setMessage("Request forget password success, please check email.");
        return  apiMessageDto;
    }

    @PostMapping(value = "/forget-password", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<Long> forgetPassword(@Valid @RequestBody ForgetPasswordForm forgetForm, BindingResult bindingResult){
        ApiMessageDto<Long> apiMessageDto = new ApiMessageDto<>();

        String[] hash = AESUtils.decrypt(forgetForm.getIdHash(),true).split(";",2);
        Long id = ConvertUtils.convertStringToLong(hash[0]);
        if(id <= 0){
            throw new BadRequestException("Incorrect hash verification", ErrorCode.ACCOUNT_ERROR_INCORRECT_HASH_VERIFICATION);
        }

        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Account not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND));

        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Customer not found", ErrorCode.CUSTOMER_ERROR_NOT_FOUND));

        if(customer.getOtpFailCount() >= BarberConstant.MAX_OTP_INVALID_COUNT){
            apiMessageDto.setResult(false);
            apiMessageDto.setCode(ErrorCode.ACCOUNT_ERROR_LOCKED);
            return apiMessageDto;
        }

        if (!Objects.equals(BarberConstant.STATUS_PENDING, account.getStatus())){
            throw new BadRequestException("Account cannot be verified", ErrorCode.ACCOUNT_ERROR_VERIFY_FAILED);
        }

        if(customer.getOtpFailCount() >= BarberConstant.MAX_OTP_INVALID_COUNT){
            account.setStatus(BarberConstant.STATUS_LOCK);
            accountRepository.save(account);
            throw new BadRequestException("Account has been locked", ErrorCode.ACCOUNT_ERROR_LOCKED);
        }

        if(!customer.getOtp().equals(forgetForm.getOtp()) ||
            (new Date().getTime() - customer.getOtpCreatedAt().getTime() >= BarberConstant.MAX_TIME_OTP_CREATED)){
            customer.setOtpFailCount(customer.getOtpFailCount() + 1);
            customerRepository.save(customer);
            throw new BadRequestException("OTP invalid", ErrorCode.ACCOUNT_ERROR_OPT_INVALID);
        }

        account.setStatus(BarberConstant.STATUS_ACTIVE);
        account.setPassword(passwordEncoder.encode(forgetForm.getNewPassword()));
        accountRepository.save(account);

        customer.setOtp(null);
        customer.setOtpFailCount(0);
        customer.setOtpCreatedAt(null);
        customerRepository.save(customer);

        apiMessageDto.setMessage("Change password success.");
        return  apiMessageDto;
    }

    private void sendReSendVerifyAccount(Account account, Customer customer){
        String subject = "Gửi lại xác thực";
        String html = "<div style=\"font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; background-color: #f9f9f9;\">" +
            "<h2 style=\"color: #2c3e50; text-align: center;\">Chào bạn!</h2>" +
            "<p style=\"font-size: 16px; line-height: 1.6;\">Bạn vừa yêu cầu gửi lại mã OTP. Mã mới của bạn là:</p>" +
            "<p style=\"font-size: 18px; font-weight: bold; text-align: center; color: #e74c3c; background-color: #fff; border: 1px dashed #e74c3c; padding: 10px; border-radius: 4px;\">" + customer.getOtp() + "</p>" +
            "<p style=\"font-size: 16px; line-height: 1.6;\">Vui lòng nhập mã này vào trang xác thực để kích hoạt tài khoản.</p>" +
            "<p style=\"font-size: 16px; line-height: 1.6; color: #555;\">Mã OTP có hiệu lực trong <strong>5 phút</strong>.</p>" +
            "<p style=\"font-size: 14px; color: #999; font-style: italic;\">Nếu bạn không thực hiện hành động này, hãy bỏ qua email này.</p>" +
            "</div>";
        barberApiService.sendEmail(account.getEmail(), html, subject, true);
    }

    private void sendForgetPassword(Account account, Customer customer){
        String subject = "Yêu cầu đặt lại mật khẩu";
        String html = "<div style=\"font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: auto; border: 1px solid #ddd; padding: 20px; border-radius: 8px; background-color: #f9f9f9;\">" +
            "<h2 style=\"color: #2c3e50; text-align: center;\">Chào bạn!</h2>" +
            "<p style=\"font-size: 16px; line-height: 1.6;\">Bạn vừa yêu cầu gửi mã OTP cho việc lấy lại mật khẩu. Mã OTP của bạn là:</p>" +
            "<p style=\"font-size: 18px; font-weight: bold; text-align: center; color: #e74c3c; background-color: #fff; border: 1px dashed #e74c3c; padding: 10px; border-radius: 4px;\">" + customer.getOtp() + "</p>" +
            "<p style=\"font-size: 16px; line-height: 1.6;\">Vui lòng nhập mã này vào trang xác thực để khôi phục mật khẩu.</p>" +
            "<p style=\"font-size: 16px; line-height: 1.6; color: #555;\">Mã OTP có hiệu lực trong <strong>5 phút</strong>.</p>" +
            "<p style=\"font-size: 14px; color: #999; font-style: italic;\">Nếu bạn không thực hiện hành động này, hãy bỏ qua email này.</p>" +
            "</div>";
        barberApiService.sendEmail(account.getEmail(), html, subject, true);
    }
}
