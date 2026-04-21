package com.barber.api.model.criteria;

import com.barber.api.model.BookingService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

@Data
public class BookingServiceCriteria {
  private static final long serialVersionUID = 1L;
  private Long id;
  private Long bookingId;

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
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    };
  }
}
