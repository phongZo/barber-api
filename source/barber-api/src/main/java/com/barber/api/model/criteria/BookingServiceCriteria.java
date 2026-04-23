package com.barber.api.model.criteria;

import com.barber.api.model.Booking;
import com.barber.api.model.BookingService;
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
public class BookingServiceCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private Long bookingId;
  private Long customerId;
  private String email;

  public Specification<BookingService> getSpecification() {
    return new Specification<BookingService>() {
      private static final long serialVersionUID = 1L;

      @Override
      public Predicate toPredicate(Root<BookingService> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (getId() != null) {
          predicates.add(cb.equal(root.get("id"), getId()));
        }

        if (getBookingId() != null){
          predicates.add(cb.equal(root.get("booking").get("id"), getBookingId()));
        }

        if (getCustomerId() != null){
          Join<BookingService, Booking> bookingJoin = root.join("booking");
          predicates.add(cb.equal(bookingJoin.get("customer").get("id"), getCustomerId()));
        }

        if (StringUtils.isNotBlank(getEmail())){
          predicates.add(cb.equal(
                  cb.function(
                      "JSON_EXTRACT",
                      String.class,
                      root.get("booking").get("customerInfo"),
                      cb.literal("$.email")
                  ),
                  getEmail()
              )
          );
        }
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
