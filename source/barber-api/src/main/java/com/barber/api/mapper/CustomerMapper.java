package com.barber.api.mapper;

import com.barber.api.dto.customer.CustomerDto;
import com.barber.api.dto.customer.CustomerProfileDto;
import com.barber.api.form.customer.SignUpCustomerForm;
import com.barber.api.model.Customer;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {AccountMapper.class})
public interface CustomerMapper {
  @Mapping(source = "birthday", target = "birthday")
  @BeanMapping(ignoreByDefault = true)
  Customer fromSignUpCustomerFormToEntity(SignUpCustomerForm signUpCustomerForm);

  @Mapping(source = "id", target = "id")
  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCustomerDto")
  CustomerDto fromEntityToCustomerDto(Customer customer);

  @IterableMapping(elementTargetType = CustomerDto.class, qualifiedByName = "fromEntityToCustomerDto")
  List<CustomerDto> fromEntityToCustomerDtoList(List<Customer> customers);

  @Mapping(source = "birthday", target = "birthday")
  @Mapping(source = "score", target = "score")
  @Mapping(source = "account", target = "account", qualifiedByName = "fromEntityToAccountUserDto")
  @BeanMapping(ignoreByDefault = true)
  @Named("fromEntityToCustomerProfileDto")
  CustomerProfileDto fromEntityToCustomerProfileDto(Customer customer);
}
