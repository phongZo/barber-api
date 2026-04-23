package com.barber.api.model.criteria;

import com.barber.api.model.Account;
import com.barber.api.model.Booking;
import com.barber.api.model.Branch;
import com.barber.api.model.Customer;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

@Data
public class BookingCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private Long customerId;
  private String username;
  private Long branchId;
  private String email;
  private Integer status;

  public Specification<Booking> getSpecification() {
    return new Specification<Booking>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<Booking> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          predicates.add(cb.equal(root.get("id"), getId()));
        }

        if (getCustomerId() != null){
          predicates.add(cb.equal(root.get("customer").get("id"), getCustomerId()));
        }

        if (StringUtils.isNotEmpty(getUsername())){
          Join<Booking, Customer> customerJoin = root.join("customer");
          Join<Customer, Account> accountJoin = customerJoin.join("account");
          predicates.add(cb.like(cb.lower(accountJoin.get("username")), "%" + getUsername().toLowerCase() + "%"));
        }

        if (getStatus() != null){
          predicates.add(cb.equal(root.get("status"), getStatus()));
        }

        if (getBranchId() != null){
          Join<Booking, Branch> branchJoin = root.join("branch");
          predicates.add(cb.equal(branchJoin.get("id"), getBranchId()));
        }

        if (StringUtils.isNotBlank(getEmail())) {
          predicates.add(cb.equal(
              cb.function("JSON_UNQUOTE", String.class,
                  cb.function("JSON_EXTRACT", String.class,
                      root.get("customerInfo"),
                      cb.literal("$.email")
                  )
              ),
              getEmail()
          ));
        }
        query.orderBy(cb.desc(root.get("createdDate")));
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
