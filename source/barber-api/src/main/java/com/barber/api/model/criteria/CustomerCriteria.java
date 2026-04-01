package com.barber.api.model.criteria;

import com.barber.api.model.Account;
import com.barber.api.model.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@Data
public class CustomerCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String username;
  private Integer status;
  private String email;
  private String fullName;
  private String phone;

  public Specification<Customer> getSpecification() {
    return new Specification<Customer>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          Join<Customer, Account> accountJoin = root.join("account");
          predicates.add(cb.equal(accountJoin.get("id"), getId()));
        }

        if (getStatus() != null) {
          Join<Customer, Account> accountJoin = root.join("account");
          predicates.add(cb.equal(accountJoin.get("status"), getStatus()));
        }

        if (!StringUtils.isEmpty(getUsername())) {
          Join<Customer, Account> accountJoin = root.join("account");
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getUsername().toLowerCase() + "%"));
        }

        if (!StringUtils.isEmpty(getEmail())) {
          Join<Customer, Account> accountJoin = root.join("account");
          predicates.add(cb.like(cb.lower(accountJoin.get("email")), "%" + getEmail().toLowerCase() + "%"));
        }

        if (!StringUtils.isEmpty(getFullName())) {
          Join<Customer, Account> accountJoin = root.join("account");
          predicates.add(cb.like(cb.lower(accountJoin.get("fullName")), "%" + getFullName().toLowerCase() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
